package jp.co.soramitsu.load.TechicalScns

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.CoreDsl.exec
import io.gatling.javaapi.core.ScenarioBuilder
import jp.co.soramitsu.iroha2.client.blockstream.BlockStreamStorage
import jp.co.soramitsu.iroha2.client.blockstream.BlockStreamSubscription
import jp.co.soramitsu.iroha2.query.QueryBuilder
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig
import jp.co.soramitsu.load.objects.AnotherDevs
import jp.co.soramitsu.load.objects.CustomMetrics
import jp.co.soramitsu.load.toolbox.Wrench13
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlinx.coroutines.time.withTimeout
import java.time.Duration

class TransferAssetsQueryStatus: Wrench13() {
    companion object {
        @JvmStatic
        fun apply() = runBlocking {
            TransferAssetsQueryStatus().applyScn()
        }
    }

    fun applyScn(): ScenarioBuilder {
        return transferAssetsQueryStatusScn
    }

    val transferAssetsQueryStatusScn = CoreDsl.scenario("TransferAssetsQueryStatus")
        .repeat(SimulationConfig.simulation.attemptsToTransaction)
        .on(
            exec { Session ->
                var currentDevIndex: Int = Random.nextInt(0, AnotherDevs.list.size - 1)
                currentDevAccountId = AnotherDevs.list[currentDevIndex].anotherDevAccountId
                currentDevKeyPair = AnotherDevs.list[currentDevIndex].anotherDevKeyPair
                currentDevAssetId = AnotherDevs.list[currentDevIndex].anotherDevAssetId
                currentDevAssetIdBeforeTransferring = AnotherDevs.list[currentDevIndex].assetValue
                do {
                    var targetDevIndex: Int = Random.nextInt(0, AnotherDevs.list.size - 1)
                    targetDevAccountId = AnotherDevs.list[targetDevIndex].anotherDevAccountId
                    targetDevKeyPair = AnotherDevs.list[targetDevIndex].anotherDevKeyPair
                } while (targetDevAccountId.name.string == currentDevAccountId.name.string)
                Session
            }
            .exec { Session ->
                val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
                timer = CustomMetrics.findAssetsByAccountIdQueryTimer.labels(
                    "gatling"
                    , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                    , Iroha2SetUp::class.simpleName
                ).startTimer()
                try {
                    runBlocking {
                        QueryBuilder.findAssetsByAccountId(accountId = currentDevAccountId)
                            .account(currentDevAccountId)
                            .buildSigned(currentDevKeyPair)
                            .let { query ->
                                iroha2Client.sendQuery(query)
                                pliers.healthCheck(true, "TransferAssetsQueryStatus")
                            }
                    }
                } finally {
                    timer.observeDuration()
                    CustomMetrics.findAssetsByAccountIdQueryCount.labels(
                        "gatling"
                        , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                        , Iroha2SetUp::class.simpleName).inc()
                    sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryCount, "query")
                    sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryTimer, "query")
                }
                Thread.sleep(queryWaiter)
                Session
            }
            .exec { Session ->
                val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
                timer = CustomMetrics.findAssetsByAccountIdQueryTimer.labels(
                    "gatling"
                    , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                    , Iroha2SetUp::class.simpleName
                ).startTimer()
                try {
                    runBlocking {
                        QueryBuilder.findAssetsByAccountId(accountId = targetDevAccountId)
                            .account(targetDevAccountId)
                            .buildSigned(targetDevKeyPair)
                            .let { query ->
                                iroha2Client.sendQuery(query)
                            }.let {
                                targetDevAssetIdAfterTransferring = it.get(0).value
                            }
                    }
                } finally {
                    timer.observeDuration()
                    CustomMetrics.findAssetsByAccountIdQueryCount.labels(
                        "gatling"
                        , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                        , Iroha2SetUp::class.simpleName).inc()
                    sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryCount, "query")
                    sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryTimer, "query")
                }
                Thread.sleep(queryWaiter)
                attempt++
                if(attempt % SimulationConfig.simulation.attemptsToTransferTransaction == 0){
                    val newSession = Session.set("condition", true)
                    newSession
                } else {
                    val newSession = Session.set("condition", false)
                    newSession
                }
            }.doIf{ Session -> Session.getBoolean("condition") }
                .then(
                    exec { Session ->
                        val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
                        timer = CustomMetrics.subscriptionToBlockStreamTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName
                        ).startTimer()
                        try {
                            val idToSubscription: Pair<Iterable<BlockStreamStorage>, BlockStreamSubscription> = iroha2Client.subscribeToBlockStream(1, 2)
                            subscription = idToSubscription.component2()
                        } finally {
                            timer.observeDuration()
                            CustomMetrics.subscriptionToBlockStreamCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            sendMetricsToPrometheus(CustomMetrics.subscriptionToBlockStreamCount, "transaction")
                            sendMetricsToPrometheus(CustomMetrics.subscriptionToBlockStreamTimer, "transaction")
                        }
                        timer = CustomMetrics.transferAssetTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName
                        ).startTimer()
                        try {
                            runBlocking {
                                iroha2Client.sendTransaction {
                                    account(currentDevAccountId)
                                    transferAsset(currentDevAssetId, 1, targetDevAccountId)
                                    buildSigned(currentDevKeyPair)
                                }.also { d ->
                                    withTimeout(Duration.ofSeconds(transactionWaiter)) { d.await() }
                                }
                                subscription.close()
                            }
                        } catch (ex: RuntimeException) {
                            println(ex.message)
                        } finally {
                            timer.observeDuration()
                            CustomMetrics.transferAssetCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            sendMetricsToPrometheus(CustomMetrics.transferAssetCount, "transaction")
                            sendMetricsToPrometheus(CustomMetrics.transferAssetTimer, "transaction")
                        }
                        val newSession = Session.set("condition", false)
                        newSession
                    }
                    .exec { Session ->
                        val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
                        timer = CustomMetrics.findAssetsByAccountIdQueryTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName
                        ).startTimer()
                        try {
                            runBlocking {
                                QueryBuilder.findAssetsByAccountId(accountId = currentDevAccountId)
                                    .account(currentDevAccountId)
                                    .buildSigned(currentDevKeyPair)
                                    .let { query ->
                                        iroha2Client.sendQuery(query)
                                    }.let {
                                        currentDevAssetIdAfterTransferring = it.get(0).value
                                    }
                            }
                        } finally {
                            timer.observeDuration()
                            CustomMetrics.findAssetsByAccountIdQueryCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryCount, "query")
                            sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryTimer, "query")
                        }
                        Thread.sleep(queryWaiter)
                        Session
                    }
                    .exec { Session ->
                        val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
                        timer = CustomMetrics.findAssetsByAccountIdQueryTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName
                        ).startTimer()
                        try {
                            runBlocking {
                                QueryBuilder.findAssetsByAccountId(accountId = targetDevAccountId)
                                    .account(targetDevAccountId)
                                    .buildSigned(targetDevKeyPair)
                                    .let { query ->
                                        iroha2Client.sendQuery(query)
                                    }.let {
                                        targetDevAssetIdAfterTransferring = it.get(0).value
                                    }
                            }
                        } finally {
                            timer.observeDuration()
                            CustomMetrics.findAssetsByAccountIdQueryCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryCount, "query")
                            sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryTimer, "query")
                        }
                        Thread.sleep(queryWaiter)
                        Session
                    }
                )
        )
}
