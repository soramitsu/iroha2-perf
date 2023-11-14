package jp.co.soramitsu.load

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.ScenarioBuilder
import jp.co.soramitsu.iroha2.asDomainId
import jp.co.soramitsu.iroha2.asName
import jp.co.soramitsu.iroha2.client.Iroha2Client
import jp.co.soramitsu.iroha2.client.blockstream.BlockStreamStorage
import jp.co.soramitsu.iroha2.client.blockstream.BlockStreamSubscription
import jp.co.soramitsu.iroha2.generateKeyPair
import jp.co.soramitsu.iroha2.generated.*
import jp.co.soramitsu.iroha2.toIrohaPublicKey
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig
import jp.co.soramitsu.load.objects.AnotherDev
import jp.co.soramitsu.load.objects.AnotherDevs
import jp.co.soramitsu.load.objects.CustomHistogram
import jp.co.soramitsu.load.toolbox.Wrench13
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.withTimeout
import java.time.Duration
import java.util.*
import kotlin.Pair

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
            /*val randomIndex = (0 until peers.size).random()
            val randomPeer = peers[randomIndex]
            Iroha2Client = buildClient(randomPeer)*/

            timer = CustomHistogram.subscriptionToBlockStreamTimer.labels(
                "gatling"
                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                , Iroha2SetUp::class.simpleName).startTimer()
            try {
                val idToSubscription: Pair<Iterable<BlockStreamStorage>, BlockStreamSubscription> = Iroha2Client.subscribeToBlockStream(1, 2)
                subscription = idToSubscription.component2()
            } finally {
                timer.observeDuration()
                CustomHistogram.subscriptionToBlockStreamCount.labels(
                    "gatling"
                    , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                    , Iroha2SetUp::class.simpleName).inc()
                //sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStreamCount, "transaction")
                //sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStreamTimer, "transaction")
            }
            val anotherDevDomainId = "bulb_${UUID.randomUUID()}_${UUID.randomUUID()}".asDomainId()
            //anotherDevDomainIdList.add(anotherDevDomainId)
            timer = CustomHistogram.domainRegisterTimer.labels(
                "gatling"
                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                , Iroha2SetUp::class.simpleName).startTimer()
            try {
                runBlocking {
                    Iroha2Client.sendTransaction {
                        account(admin)
                        registerDomain(anotherDevDomainId)
                        buildSigned(adminKeyPair)
                    }.also { d ->
                        withTimeout(Duration.ofSeconds(transactionWaiter))
                        { d.await()
                            println("SEND_TRANSACTION: DOMAIN REGISTER")
                            anotherDevDomainIdList.add(anotherDevDomainId)
                            timer.observeDuration()
                            CustomHistogram.domainRegisterCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            //sendMetricsToPrometheus(CustomHistogram.domainRegisterCount, "transaction")
                            //sendMetricsToPrometheus(CustomHistogram.domainRegisterTimer, "transaction")
                        }
                    }
                }
                subscription.close()
            } catch (ex: RuntimeException) {
                println(ex.message)
            }
            Session
        }
        .foreach(anotherDevDomainIdList, "domainId").on(
                //accounts on each domain = threads * anotherDevDomainIdList.size * setUpUsersOnEachDomain
                repeat(SimulationConfig.simulation.setUpUsersOnEachDomain).on(
                    exec { Session ->
                        /*val randomIndex = (0 until peers.size).random()
                        val randomPeer = peers[randomIndex]
                        Iroha2Client = buildClient(randomPeer)*/
                        timer = CustomHistogram.subscriptionToBlockStreamTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName).startTimer()
                        try {
                            val idToSubscription: Pair<Iterable<BlockStreamStorage>, BlockStreamSubscription> = Iroha2Client.subscribeToBlockStream(1, 2)
                            subscription = idToSubscription.component2()
                        } finally {
                            timer.observeDuration()
                            CustomHistogram.subscriptionToBlockStreamCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            //sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStreamCount, "transaction")
                            //sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStreamTimer, "transaction")
                        }
                        val anotherDev = AnotherDev()
                        anotherDev.anotherDevDomainId = Session.get<DomainId>("domainId")!!
                        anotherDev.anotherDevAccountId = AccountId(
                            anotherDev.anotherDevDomainId,
                            Name("anotherDev${UUID.randomUUID()}_${UUID.randomUUID()}")
                        )
                        anotherDev.anotherDevKeyPair = generateKeyPair()
                        timer = CustomHistogram.accountRegisterTimer.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).startTimer()
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
                                    withTimeout(Duration.ofSeconds(transactionWaiter)) {
                                        d.await()
                                        println("SEND_TRANSACTION: ACCOUNT REGISTER")
                                    }
                                }
                                subscription.close()
                            }
                        } catch (ex: RuntimeException) {
                            println(ex.message)
                        } finally {
                            timer.observeDuration()
                            CustomHistogram.accountRegisterCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            //sendMetricsToPrometheus(CustomHistogram.accountRegisterCount, "transaction")
                            //sendMetricsToPrometheus(CustomHistogram.accountRegisterTimer, "transaction")
                        }
                        Thread.sleep(5000)
                        timer = CustomHistogram.subscriptionToBlockStreamTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName).startTimer()
                        try {
                            val idToSubscription: Pair<Iterable<BlockStreamStorage>, BlockStreamSubscription> = Iroha2Client.subscribeToBlockStream(1, 2)
                            subscription = idToSubscription.component2()
                        } finally {
                            timer.observeDuration()
                            CustomHistogram.subscriptionToBlockStreamCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            //sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStreamCount, "transaction")
                            //sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStreamTimer, "transaction")
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

                            runBlocking {
                                Iroha2Client.sendTransaction {
                                    account(anotherDev.anotherDevAccountId)
                                    registerAssetDefinition(anotherDev.assetDefinitionId, AssetValueType.Quantity())
                                    buildSigned(anotherDev.anotherDevKeyPair)
                                }.also { d ->
                                    withTimeout(Duration.ofSeconds(transactionWaiter)) {
                                        d.await()
                                        println("SEND_TRANSACTION: ASSET DEFINITION REGISTER")
                                    }
                                }
                                subscription.close()
                            }
                        } catch (ex: RuntimeException) {
                            println(ex.message)
                        } finally {
                            timer.observeDuration()
                            CustomHistogram.assetDefinitionRegisterCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            //sendMetricsToPrometheus(CustomHistogram.assetDefinitionRegisterCount, "transaction")
                            //sendMetricsToPrometheus(CustomHistogram.assetDefinitionRegisterTimer, "transaction")
                        }
                        Thread.sleep(5000)
                        timer = CustomHistogram.subscriptionToBlockStreamTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName).startTimer()
                        try {
                            val idToSubscription: Pair<Iterable<BlockStreamStorage>, BlockStreamSubscription> = Iroha2Client.subscribeToBlockStream(1, 2)
                            subscription = idToSubscription.component2()
                        } finally {
                            timer.observeDuration()
                            CustomHistogram.subscriptionToBlockStreamCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            //sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStreamCount, "transaction")
                            //sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStreamTimer, "transaction")
                        }
                        anotherDev.anotherDevAssetId = AssetId(anotherDev.assetDefinitionId, anotherDev.anotherDevAccountId)
                        anotherDev.assetValue = AssetValue.Quantity(10000)
                        timer = CustomHistogram.assetMintTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName).startTimer()
                        try {
                            runBlocking {
                                Iroha2Client.sendTransaction {
                                    account(anotherDev.anotherDevAccountId)
                                    mintAsset(anotherDev.anotherDevAssetId, 10000)
                                    buildSigned(anotherDev.anotherDevKeyPair)
                                }.also { d ->
                                    withTimeout(Duration.ofSeconds(transactionWaiter)) {
                                        d.await()
                                        println("SEND_TRANSACTION: MINT ASSET")
                                    }
                                }
                                subscription.close()
                            }
                        } catch (ex: RuntimeException) {
                            println(ex.message)
                        } finally {
                            timer.observeDuration()
                            CustomHistogram.assetMintCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            //sendMetricsToPrometheus(CustomHistogram.assetMintCount, "transaction")
                            //sendMetricsToPrometheus(CustomHistogram.assetMintTimer, "transaction")
                        }
                        Thread.sleep(5000)
                        AnotherDevs.list.add(anotherDev)
                        Session
                    }
                )
            )
}