package jp.co.soramitsu.load

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.ScenarioBuilder
import jp.co.soramitsu.iroha2.asDomainId
import jp.co.soramitsu.iroha2.asName
import jp.co.soramitsu.iroha2.client.blockstream.BlockStreamStorage
import jp.co.soramitsu.iroha2.client.blockstream.BlockStreamSubscription
import jp.co.soramitsu.iroha2.generateKeyPair
import jp.co.soramitsu.iroha2.generated.*
import jp.co.soramitsu.iroha2.toIrohaPublicKey
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig
import jp.co.soramitsu.load.objects.AnotherDev
import jp.co.soramitsu.load.objects.AnotherDevs
import jp.co.soramitsu.load.objects.CustomMetrics
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

    //TODO: напили в каунтере теги для каждого счетчика
    // в теги сложи параметры запуска теста
    // для дальнейшего отслеживания на макро уровне
    // нескольких тестов для быстрого получения инфы

    val iroha2SetUpScn = scenario("Iroha2SetUp")
        .exec { Session ->
            val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
            timer = CustomMetrics.subscriptionToBlockStreamTimer.labels(
                "gatling"
                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                , Iroha2SetUp::class.simpleName).startTimer()
            try {
                val idToSubscription: Pair<Iterable<BlockStreamStorage>, BlockStreamSubscription> = iroha2Client.subscribeToBlockStream(1, 2)
                subscription = idToSubscription.component2()
                CustomMetrics.subscriptionToBlockStreamCount.labels(
                    "gatling"
                    , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                    , Iroha2SetUp::class.simpleName).inc()
                sendMetricsToPrometheus(CustomMetrics.subscriptionToBlockStreamCount, "transaction")
            } catch (ex: Throwable) {
                println(ex.message)
                CustomMetrics.subscriptionToBlockStreamErrorCount.labels(
                    "gatling"
                    , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                    , Iroha2SetUp::class.simpleName).inc()
                sendMetricsToPrometheus(CustomMetrics.subscriptionToBlockStreamErrorCount, "transaction")
            } finally {
                timer.observeDuration()
                sendMetricsToPrometheus(CustomMetrics.subscriptionToBlockStreamTimer, "transaction")
            }
            val anotherDevDomainId = "bulb_${UUID.randomUUID()}_${UUID.randomUUID()}".asDomainId()
            timer = CustomMetrics.domainRegisterTimer.labels(
                "gatling"
                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                , Iroha2SetUp::class.simpleName).startTimer()
            try {
                runBlocking {
                    iroha2Client.sendTransaction {
                        account(admin)
                        registerDomain(anotherDevDomainId)
                        buildSigned(adminKeyPair)
                    }.also { d ->
                        withTimeout(Duration.ofSeconds(transactionWaiter))
                        { d.await()
                            pliers.healthCheck(true, "Iroha2SetUp")
                            anotherDevDomainIdList.add(anotherDevDomainId)
                            timer.observeDuration()
                            CustomMetrics.domainRegisterCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            sendMetricsToPrometheus(CustomMetrics.domainRegisterCount, "transaction")
                            sendMetricsToPrometheus(CustomMetrics.domainRegisterTimer, "transaction")
                        }
                    }
                }
                subscription.close()
            } catch (ex: RuntimeException) {
                CustomMetrics.domainRegisterErrorCount.labels(
                    "gatling"
                    , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                    , Iroha2SetUp::class.simpleName).inc()
                sendMetricsToPrometheus(CustomMetrics.domainRegisterErrorCount, "transaction")
                println("Something went wrong on Iroha2SetUp scenario, problem with domain register transaction: " + ex.message)
                pliers.healthCheck(false, "Iroha2SetUp")
            }
            Session
        }
        .foreach(anotherDevDomainIdList, "domainId").on(
                //accounts on each domain = threads * anotherDevDomainIdList.size * setUpUsersOnEachDomain
                repeat(SimulationConfig.simulation.setUpUsersOnEachDomain).on(
                    exec { Session ->
                        val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
                        timer = CustomMetrics.subscriptionToBlockStreamTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName).startTimer()
                        try {
                            val idToSubscription: Pair<Iterable<BlockStreamStorage>, BlockStreamSubscription> = iroha2Client.subscribeToBlockStream(1, 2)
                            subscription = idToSubscription.component2()
                            CustomMetrics.subscriptionToBlockStreamCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            sendMetricsToPrometheus(CustomMetrics.subscriptionToBlockStreamCount, "transaction")
                        } catch (ex: Throwable) {
                            println(ex.message)
                            CustomMetrics.subscriptionToBlockStreamErrorCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            sendMetricsToPrometheus(CustomMetrics.subscriptionToBlockStreamErrorCount, "transaction")
                        } finally {
                            timer.observeDuration()
                            sendMetricsToPrometheus(CustomMetrics.subscriptionToBlockStreamTimer, "transaction")
                        }
                        val anotherDev = AnotherDev()
                        anotherDev.anotherDevDomainId = Session.get<DomainId>("domainId")!!
                        anotherDev.anotherDevAccountId = AccountId(
                            anotherDev.anotherDevDomainId,
                            Name("anotherDev${UUID.randomUUID()}_${UUID.randomUUID()}")
                        )
                        anotherDev.anotherDevKeyPair = generateKeyPair()
                        timer = CustomMetrics.accountRegisterTimer.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).startTimer()
                        try {
                            runBlocking {
                                iroha2Client.sendTransaction {
                                    account(admin)
                                    registerAccount(
                                        anotherDev.anotherDevAccountId,
                                        listOf(anotherDev.anotherDevKeyPair.public.toIrohaPublicKey())
                                    )
                                    buildSigned(adminKeyPair)
                                }.also { d ->
                                    withTimeout(Duration.ofSeconds(transactionWaiter)) {
                                        d.await()
                                        CustomMetrics.accountRegisterCount.labels(
                                            "gatling"
                                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                            , Iroha2SetUp::class.simpleName).inc()
                                        sendMetricsToPrometheus(CustomMetrics.accountRegisterCount, "transaction")
                                    }
                                }
                                subscription.close()
                            }
                        } catch (ex: RuntimeException) {
                            CustomMetrics.accountRegisterErrorCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            sendMetricsToPrometheus(CustomMetrics.accountRegisterErrorCount, "transaction")
                            println(ex.message)
                        } finally {
                            timer.observeDuration()
                            sendMetricsToPrometheus(CustomMetrics.accountRegisterTimer, "transaction")
                        }
                        Thread.sleep(5000)
                        timer = CustomMetrics.subscriptionToBlockStreamTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName).startTimer()
                        try {
                            val idToSubscription: Pair<Iterable<BlockStreamStorage>, BlockStreamSubscription> = iroha2Client.subscribeToBlockStream(1, 2)
                            subscription = idToSubscription.component2()
                            CustomMetrics.subscriptionToBlockStreamCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            sendMetricsToPrometheus(CustomMetrics.subscriptionToBlockStreamCount, "transaction")
                        } catch (ex: Throwable) {
                            println(ex.message)
                            CustomMetrics.subscriptionToBlockStreamErrorCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            sendMetricsToPrometheus(CustomMetrics.subscriptionToBlockStreamErrorCount, "transaction")
                        } finally {
                            timer.observeDuration()
                            sendMetricsToPrometheus(CustomMetrics.subscriptionToBlockStreamTimer, "transaction")
                        }
                        anotherDev.assetDefinitionId = AssetDefinitionId(
                            "xor${UUID.randomUUID()}_${UUID.randomUUID()}".asName(),
                            anotherDev.anotherDevDomainId
                        )
                        timer = CustomMetrics.assetDefinitionRegisterTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName).startTimer()
                        try {
                            runBlocking {
                                iroha2Client.sendTransaction {
                                    account(anotherDev.anotherDevAccountId)
                                    registerAssetDefinition(anotherDev.assetDefinitionId, AssetValueType.Quantity())
                                    buildSigned(anotherDev.anotherDevKeyPair)
                                }.also { d ->
                                    withTimeout(Duration.ofSeconds(transactionWaiter)) {
                                        d.await()
                                        CustomMetrics.assetDefinitionRegisterCount.labels(
                                            "gatling"
                                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                            , Iroha2SetUp::class.simpleName).inc()
                                        sendMetricsToPrometheus(CustomMetrics.assetDefinitionRegisterCount, "transaction")
                                    }
                                }
                                subscription.close()
                            }
                        } catch (ex: RuntimeException) {
                            CustomMetrics.assetDefinitionRegisterErrorCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            sendMetricsToPrometheus(CustomMetrics.assetDefinitionRegisterErrorCount, "transaction")
                            println(ex.message)
                        } finally {
                            timer.observeDuration()
                            sendMetricsToPrometheus(CustomMetrics.assetDefinitionRegisterTimer, "transaction")
                        }
                        Thread.sleep(5000)
                        timer = CustomMetrics.subscriptionToBlockStreamTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName).startTimer()
                        try {
                            val idToSubscription: Pair<Iterable<BlockStreamStorage>, BlockStreamSubscription> = iroha2Client.subscribeToBlockStream(1, 2)
                            subscription = idToSubscription.component2()
                            CustomMetrics.subscriptionToBlockStreamCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            sendMetricsToPrometheus(CustomMetrics.subscriptionToBlockStreamCount, "transaction")
                        } catch (ex: Throwable) {
                            println(ex.message)
                            CustomMetrics.subscriptionToBlockStreamErrorCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            sendMetricsToPrometheus(CustomMetrics.subscriptionToBlockStreamErrorCount, "transaction")
                        } finally {
                            timer.observeDuration()
                            sendMetricsToPrometheus(CustomMetrics.subscriptionToBlockStreamTimer, "transaction")
                        }
                        anotherDev.anotherDevAssetId = AssetId(anotherDev.assetDefinitionId, anotherDev.anotherDevAccountId)
                        anotherDev.assetValue = AssetValue.Quantity(10000)
                        timer = CustomMetrics.assetMintTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName).startTimer()
                        try {
                            runBlocking {
                                iroha2Client.sendTransaction {
                                    account(anotherDev.anotherDevAccountId)
                                    mintAsset(anotherDev.anotherDevAssetId, 10000)
                                    buildSigned(anotherDev.anotherDevKeyPair)
                                }.also { d ->
                                    withTimeout(Duration.ofSeconds(transactionWaiter)) {
                                        d.await()
                                        CustomMetrics.assetMintCount.labels(
                                            "gatling"
                                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                            , Iroha2SetUp::class.simpleName).inc()
                                        sendMetricsToPrometheus(CustomMetrics.assetMintCount, "transaction")
                                    }
                                }
                                subscription.close()
                            }
                        } catch (ex: RuntimeException) {
                            CustomMetrics.assetMintErrorCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            sendMetricsToPrometheus(CustomMetrics.assetMintErrorCount, "transaction")
                            println(ex.message)
                        } finally {
                            timer.observeDuration()
                            sendMetricsToPrometheus(CustomMetrics.assetMintTimer, "transaction")
                        }
                        Thread.sleep(5000)
                        AnotherDevs.list.add(anotherDev)
                        Session
                    }
                )
            )
}