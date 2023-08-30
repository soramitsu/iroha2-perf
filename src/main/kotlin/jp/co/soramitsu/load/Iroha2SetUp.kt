package jp.co.soramitsu.load

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.ScenarioBuilder
import jp.co.soramitsu.iroha2.*
import jp.co.soramitsu.iroha2.generated.*
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig
import jp.co.soramitsu.load.objects.AnotherDev
import jp.co.soramitsu.load.objects.AnotherDevs
import jp.co.soramitsu.load.objects.CustomHistogram
import jp.co.soramitsu.load.toolbox.Wrench13
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.withTimeout
import java.time.Duration
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
            timer = CustomHistogram.subscriptionToBlockStream.labels(
                "gatling"
                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                , Iroha2SetUp::class.simpleName
            ).startTimer()
            try {
                pliers.getSubscribetionToBlockStream(Iroha2Client, 1, 2)
            } finally {
                timer.observeDuration()
                sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStream, "subscription")
            }

            anotherDevDomainId = "bulb_${UUID.randomUUID()}_${UUID.randomUUID()}".asDomainId()
            anotherDevDomainIdList.add(anotherDevDomainId)

            timer = CustomHistogram.domainRegisterTransactionTimer.labels(
                "gatling"
                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                , Iroha2SetUp::class.simpleName
            ).startTimer()
            try {
                runBlocking {
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
                sendMetricsToPrometheus(CustomHistogram.domainRegisterTransactionTimer, "transaction")
            }

            Thread.sleep(4000)
            /*runBlocking {
                pliers.getLastDomain()*/
            Session
        }
        .foreach(anotherDevDomainIdList, "domainId").on(
                repeat(SimulationConfig.simulation.setUpUsersOnEachDomain).on(
                    exec { Session ->
                        timer = CustomHistogram.subscriptionToBlockStream.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName
                        ).startTimer()
                        try {
                            pliers.getSubscribetionToBlockStream(Iroha2Client, 1, 2)
                        } finally {
                            timer.observeDuration()
                            sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStream, "subscription")
                        }
                        var anotherDev = AnotherDev()
                        anotherDev.anotherDevDomainId = Session.get<DomainId>("domainId")!!
                        anotherDev.anotherDevAccountId = AccountId(
                            Name("anotherDev${UUID.randomUUID()}_${UUID.randomUUID()}"),
                            anotherDev.anotherDevDomainId
                        )
                        anotherDev.anotherDevKeyPair = generateKeyPair()
                        timer = CustomHistogram.accountRegisterTransactionTimer
                            .labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName
                            ).startTimer()
                        try {
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
                            sendMetricsToPrometheus(CustomHistogram.accountRegisterTransactionTimer, "transaction")
                        }
                        Thread.sleep(4000)
                        /*runBlocking {
                            pliers.getLastDomain()
                        }*/
                        timer = CustomHistogram.subscriptionToBlockStream.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName
                        ).startTimer()
                        try {
                            pliers.getSubscribetionToBlockStream(Iroha2Client, 1, 2)
                        } finally {
                            timer.observeDuration()
                            sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStream, "subscription")
                        }
                        anotherDev.assetDefinitionId = AssetDefinitionId(
                            "xor${UUID.randomUUID()}_${UUID.randomUUID()}".asName(),
                            anotherDev.anotherDevDomainId
                        )
                        timer = CustomHistogram.assetDefinitionRegisterTransactionTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName
                        ).startTimer()
                        try {
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
                            sendMetricsToPrometheus(CustomHistogram.assetDefinitionRegisterTransactionTimer, "transaction")
                        }
                        Thread.sleep(4000)
                        /*runBlocking {
                            pliers.getLastDomain()
                        }*/
                        timer = CustomHistogram.subscriptionToBlockStream.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName
                        ).startTimer()
                        try {
                            pliers.getSubscribetionToBlockStream(Iroha2Client, 1, 2)
                        } finally {
                            timer.observeDuration()
                            sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStream, "subscription")
                        }
                        anotherDev.anotherDevAssetId = AssetId(anotherDev.assetDefinitionId, anotherDev.anotherDevAccountId)
                        timer = CustomHistogram.assetMintTransactionTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName
                        ).startTimer()
                        try {
                            runBlocking {
                                Iroha2Client.sendTransaction {
                                    account(anotherDev.anotherDevAccountId)
                                    mintAsset(anotherDev.anotherDevAssetId, 100)
                                    buildSigned(anotherDev.anotherDevKeyPair)
                                }.also { d ->
                                    withTimeout(Duration.ofSeconds(transactionWaiter)) { d.await() }
                                }
                            }
                        } finally {
                            timer.observeDuration()
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