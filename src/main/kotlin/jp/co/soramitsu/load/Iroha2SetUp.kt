package jp.co.soramitsu.load

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.ScenarioBuilder
import io.prometheus.client.Counter
import kotlinx.coroutines.time.withTimeout
import java.time.Duration
import jp.co.soramitsu.iroha2.asDomainId
import jp.co.soramitsu.iroha2.asName
import jp.co.soramitsu.iroha2.generateKeyPair
import jp.co.soramitsu.iroha2.generated.*
import jp.co.soramitsu.iroha2.toIrohaPublicKey
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
            /*counter = CustomHistogram.subscriptionToBlockStream.labels(
                "gatling"
                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                , Iroha2SetUp::class.simpleName
            )*/
            try {
                Iroha2Client.subscribeToBlockStream(1,2,true)
            } finally {
                CustomHistogram.subscriptionToBlockStream.inc()
                sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStream, "subscription")
            }

            anotherDevDomainId = "bulb_${UUID.randomUUID()}_${UUID.randomUUID()}".asDomainId()
            anotherDevDomainIdList.add(anotherDevDomainId)
            /*counter = CustomHistogram.domainRegisterTransactionTimer.labels(
                "gatling"
                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                , Iroha2SetUp::class.simpleName
            )*/
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
                CustomHistogram.domainRegisterTransactionTimer.inc()
                sendMetricsToPrometheus(CustomHistogram.domainRegisterTransactionTimer, "transaction")
            }

            Thread.sleep(4000)
            /*runBlocking {
                pliers.getLastDomain()*/
            Session
        }
        .foreach(anotherDevDomainIdList, "domainId").on(
                //accounts on each domain = threads * anotherDevDomainIdList.size * setUpUsersOnEachDomain
                repeat(SimulationConfig.simulation.setUpUsersOnEachDomain).on(
                    exec { Session ->
                        /*counter = CustomHistogram.subscriptionToBlockStream.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName
                        )*/
                        try {
                            Iroha2Client.subscribeToBlockStream(1,2,true)
                        } finally {
                            CustomHistogram.subscriptionToBlockStream.inc()
                            sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStream, "subscription")
                        }
                        val anotherDev = AnotherDev()
                        anotherDev.anotherDevDomainId = Session.get<DomainId>("domainId")!!
                        anotherDev.anotherDevAccountId = AccountId(
                            Name("anotherDev${UUID.randomUUID()}_${UUID.randomUUID()}"),
                            anotherDev.anotherDevDomainId
                        )
                        anotherDev.anotherDevKeyPair = generateKeyPair()
                        /*counter = CustomHistogram.accountRegisterTransactionTimer
                            .labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName
                            )*/
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
                            CustomHistogram.accountRegisterTransactionTimer.inc()
                            sendMetricsToPrometheus(CustomHistogram.accountRegisterTransactionTimer, "transaction")
                        }
                        Thread.sleep(4000)
                        /*runBlocking {
                            pliers.getLastDomain()
                        }*/
                        /*counter = CustomHistogram.subscriptionToBlockStream.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName
                        )*/
                        try {
                            Iroha2Client.subscribeToBlockStream(1,2,true)
                        } finally {
                            CustomHistogram.subscriptionToBlockStream.inc()
                            sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStream, "subscription")
                        }
                        anotherDev.assetDefinitionId = AssetDefinitionId(
                            "xor${UUID.randomUUID()}_${UUID.randomUUID()}".asName(),
                            anotherDev.anotherDevDomainId
                        )
                        /*counter = CustomHistogram.assetDefinitionRegisterTransactionTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName
                        )*/
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
                            CustomHistogram.assetDefinitionRegisterTransactionTimer.inc()
                            sendMetricsToPrometheus(CustomHistogram.assetDefinitionRegisterTransactionTimer, "transaction")
                        }
                        Thread.sleep(4000)
                        /*runBlocking {
                            pliers.getLastDomain()
                        }*/
                        /*counter = CustomHistogram.subscriptionToBlockStream.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName
                        )*/
                        try {
                            Iroha2Client.subscribeToBlockStream(1,2,true)
                        } finally {
                            CustomHistogram.subscriptionToBlockStream.inc()
                            sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStream, "subscription")
                        }
                        anotherDev.anotherDevAssetId = AssetId(anotherDev.assetDefinitionId, anotherDev.anotherDevAccountId)
                        /*counter = CustomHistogram.subscriptionToBlockStream.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName
                        )*/
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
                            CustomHistogram.assetMintTransactionTimer.inc()
                            sendMetricsToPrometheus(CustomHistogram.assetMintTransactionTimer, "transaction")
                        }
                        Thread.sleep(4000)
                        /*runBlocking {
                            pliers.getLastDomain()
                        }*/
                        AnotherDevs.list.add(anotherDev)
                        Session
                    }
                )
            )
}