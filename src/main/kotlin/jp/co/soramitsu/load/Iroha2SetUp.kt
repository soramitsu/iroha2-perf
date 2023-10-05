package jp.co.soramitsu.load

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.ScenarioBuilder
import jp.co.soramitsu.iroha2.*
import kotlinx.coroutines.time.withTimeout
import java.time.Duration
import jp.co.soramitsu.iroha2.generated.*
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig
import jp.co.soramitsu.load.objects.AnotherDev
import jp.co.soramitsu.load.objects.AnotherDevs
import jp.co.soramitsu.load.objects.CustomHistogram
import jp.co.soramitsu.load.toolbox.Wrench13
import kotlinx.coroutines.runBlocking
import java.util.*

class Iroha2SetUp : Wrench13() {
    companion object {
        @JvmStatic
        fun apply() = runBlocking {
            Iroha2SetUp().applyScn()
        }
    }

    fun applyScn(): ScenarioBuilder {
        return iroha2SetUpScn
    }

    val iroha2SetUpScn = scenario("Iroha2SetUp")
        .exec { Session ->
            timer = CustomHistogram.subscriptionToBlockStreamTimer.labels(
                "gatling"
                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                , Iroha2SetUp::class.simpleName).startTimer()
            try {
                Iroha2Client.subscribeToBlockStream(1,2,true)
            } finally {
                timer.observeDuration()
                CustomHistogram.subscriptionToBlockStreamCount.labels(
                    "gatling"
                    , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                    , Iroha2SetUp::class.simpleName).inc()
                sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStreamCount, "transaction")
                sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStreamTimer, "transaction")
            }
            anotherDevDomainId = "bulb_${UUID.randomUUID()}_${UUID.randomUUID()}".asDomainId()
            anotherDevDomainIdList.add(anotherDevDomainId)
            timer = CustomHistogram.domainRegisterTimer.labels(
                "gatling"
                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                , Iroha2SetUp::class.simpleName).startTimer()
            try {
                runBlocking {
                    println("SEND_TRANSACTION: DOMAIN REGISTER")
                    Iroha2Client.sendTransaction {
                        account(admin)
                        registerDomain(anotherDevDomainId)
                        buildSigned(adminKeyPair)
                    }.also { d ->
                        withTimeout(Duration.ofSeconds(transactionWaiter)) { d.await() }
                    }
                }
            } finally {
                timer.observeDuration()
                CustomHistogram.domainRegisterCount.labels(
                    "gatling"
                    , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                    , Iroha2SetUp::class.simpleName).inc()
                sendMetricsToPrometheus(CustomHistogram.domainRegisterCount, "transaction")
                sendMetricsToPrometheus(CustomHistogram.domainRegisterTimer, "transaction")
            }
            Session
        }
        .foreach(anotherDevDomainIdList, "domainId").on(
                //accounts on each domain = threads * anotherDevDomainIdList.size * setUpUsersOnEachDomain
                repeat(SimulationConfig.simulation.setUpUsersOnEachDomain).on(
                    exec { Session ->
                        timer = CustomHistogram.subscriptionToBlockStreamTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName).startTimer()
                        try {
                            Iroha2Client.subscribeToBlockStream(1,2,true)
                        } finally {
                            timer.observeDuration()
                            CustomHistogram.subscriptionToBlockStreamCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStreamCount, "transaction")
                            sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStreamTimer, "transaction")
                        }
                        val anotherDev = AnotherDev()
                        anotherDev.anotherDevDomainId = Session.get<DomainId>("domainId")!!
                        anotherDev.anotherDevAccountId = AccountId(
                            Name("anotherDev${UUID.randomUUID()}_${UUID.randomUUID()}"),
                            anotherDev.anotherDevDomainId
                        )
                        anotherDev.anotherDevKeyPair = generateKeyPair()
                        timer = CustomHistogram.accountRegisterTimer.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).startTimer()
                        try {
                            println("SEND_TRANSACTION: ACCOUNT REGISTER")
                            runBlocking {
                                Iroha2Client.sendTransaction {
                                    account(admin)
                                    registerAccount(
                                        anotherDev.anotherDevAccountId,
                                        listOf(anotherDev.anotherDevKeyPair.public.toIrohaPublicKey())
                                    )
                                    buildSigned(adminKeyPair)
                                }.also { d ->
                                    withTimeout(Duration.ofSeconds(transactionWaiter)) { d.await() }
                                }
                            }
                        } finally {
                            timer.observeDuration()
                            CustomHistogram.accountRegisterCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            sendMetricsToPrometheus(CustomHistogram.accountRegisterCount, "transaction")
                            sendMetricsToPrometheus(CustomHistogram.accountRegisterTimer, "transaction")
                        }
                        Thread.sleep(5000)
                        timer = CustomHistogram.subscriptionToBlockStreamTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName).startTimer()
                        try {
                            Iroha2Client.subscribeToBlockStream(1,2,true)
                        } finally {
                            timer.observeDuration()
                            CustomHistogram.subscriptionToBlockStreamCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStreamCount, "transaction")
                            sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStreamTimer, "transaction")
                        }
                        anotherDev.assetDefinitionId = AssetDefinitionId(
                            "xor${UUID.randomUUID()}_${UUID.randomUUID()}".asName(),
                            anotherDev.anotherDevDomainId
                        )
                        timer = CustomHistogram.assetDefinitionRegisterTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName).startTimer()
                        try {
                            println("SEND_TRANSACTION: ASSET DEFINITION REGISTER")
                            runBlocking {
                                Iroha2Client.sendTransaction {
                                    account(anotherDev.anotherDevAccountId)
                                    registerAssetDefinition(anotherDev.assetDefinitionId, AssetValueType.Quantity())
                                    buildSigned(anotherDev.anotherDevKeyPair)
                                }.also { d ->
                                    withTimeout(Duration.ofSeconds(transactionWaiter)) { d.await() }
                                }
                            }
                        } finally {
                            timer.observeDuration()
                            CustomHistogram.assetDefinitionRegisterCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            sendMetricsToPrometheus(CustomHistogram.assetDefinitionRegisterCount, "transaction")
                            sendMetricsToPrometheus(CustomHistogram.assetDefinitionRegisterTimer, "transaction")
                        }
                        Thread.sleep(5000)
                        timer = CustomHistogram.subscriptionToBlockStreamTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName).startTimer()
                        try {
                            Iroha2Client.subscribeToBlockStream(1,2,true)
                        } finally {
                            timer.observeDuration()
                            CustomHistogram.subscriptionToBlockStreamCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStreamCount, "transaction")
                            sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStreamTimer, "transaction")
                        }
                        anotherDev.anotherDevAssetId = AssetId(anotherDev.assetDefinitionId, anotherDev.anotherDevAccountId)
                        anotherDev.assetValue = AssetValue.Quantity(10000)
                        timer = CustomHistogram.assetMintTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName).startTimer()
                        try {
                            println("SEND_TRANSACTION: MINT ASSET")
                            runBlocking {
                                Iroha2Client.sendTransaction {
                                    account(anotherDev.anotherDevAccountId)
                                    mintAsset(anotherDev.anotherDevAssetId, 10000)
                                    buildSigned(anotherDev.anotherDevKeyPair)
                                }.also { d ->
                                    withTimeout(Duration.ofSeconds(transactionWaiter)) { d.await() }
                                }
                            }
                        } finally {
                            timer.observeDuration()
                            CustomHistogram.assetMintCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            sendMetricsToPrometheus(CustomHistogram.assetMintCount, "transaction")
                            sendMetricsToPrometheus(CustomHistogram.assetMintTimer, "transaction")
                        }
                        Thread.sleep(5000)
                        AnotherDevs.list.add(anotherDev)
                        Session
                    }
                )
            )
}