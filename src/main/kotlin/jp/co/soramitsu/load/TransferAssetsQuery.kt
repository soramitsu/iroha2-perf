package jp.co.soramitsu.load

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.CoreDsl.exec
import io.gatling.javaapi.core.ScenarioBuilder
import jp.co.soramitsu.iroha2.asAccountId
import jp.co.soramitsu.iroha2.asAssetId
import jp.co.soramitsu.iroha2.asDomainId
import jp.co.soramitsu.iroha2.client.blockstream.BlockStreamStorage
import jp.co.soramitsu.iroha2.client.blockstream.BlockStreamSubscription
import jp.co.soramitsu.iroha2.generated.AccountId
import jp.co.soramitsu.iroha2.generated.AssetId
import jp.co.soramitsu.iroha2.generated.DomainId
import jp.co.soramitsu.iroha2.query.QueryBuilder
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig
import jp.co.soramitsu.load.objects.CustomMetrics
import jp.co.soramitsu.load.toolbox.Wrench13
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.withTimeout
import java.security.KeyPair
import java.time.Duration

class TransferAssetsQuery : Wrench13() {
    lateinit var domainIdSender: DomainId
    lateinit var anotherDevAccountIdSender: AccountId
    lateinit var anotherDevAssetIdSender: AssetId
    lateinit var targetDevAccountIdReceiver: AccountId
    lateinit var anotherDevKeyPair: KeyPair

    companion object {
        @JvmStatic
        fun apply() = runBlocking {
            TransferAssetsQuery().applyScn()
        }
    }

    fun applyScn(): ScenarioBuilder {
        return transferAssetsQuery
    }

    val transferAssetsQuery = CoreDsl.scenario("TransferAssetsQuery")
        .feed(CoreDsl.csv("preconditionList.csv").circular())
        .exec { Session ->
            anotherDevKeyPair = adminKeyPair
            domainIdSender = Session.get<String>("domainIdSender")!!.asDomainId()
            anotherDevAccountIdSender = Session.get<String>("anotherDevAccountIdSender")!!.asAccountId()
            targetDevAccountIdReceiver = Session.get<String>("anotherDevAccountIdReceiver")!!.asAccountId()
            anotherDevAssetIdSender = Session.get<String>("anotherDevAssetIdSender")!!.asAssetId()
            Session
        }
        .exec { Session ->
            val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
            timer = CustomMetrics.findAssetsByAccountIdQueryTimer.labels(
                "gatling",
                System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                TransferAssetsQuery::class.simpleName
            ).startTimer()
            CustomMetrics.findAssetsByAccountIdQueryCount.labels(
                "gatling",
                System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                TransferAssetsQuery::class.simpleName
            ).inc()
            sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryCount, "query")
            try {
                runBlocking {
                    QueryBuilder.findAssetsByAccountId(anotherDevAccountIdSender)
                        .account(anotherDevAccountIdSender)
                        .buildSigned(anotherDevKeyPair)
                        .let { query ->
                            iroha2Client.sendQuery(query)
                        }
                }
            } catch (ex: RuntimeException) {
                CustomMetrics.findAssetsByAccountIdQueryErrorCount.labels(
                    "gatling",
                    System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                    TransferAssetsQuery::class.simpleName
                ).inc()
                sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryErrorCount, "query")
                println("Something went wrong on findAssetsByAccountIdQuery request, problem with find asset query: " + ex.message)
                pliers.healthCheck(false, "TransferAssets")
            } finally {
                timer.observeDuration()
                CustomMetrics.findAssetsByAccountIdQueryCount.labels(
                    "gatling",
                    System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                    TransferAssetsQuery::class.simpleName
                ).inc()
                sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryCount, "query")
                sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryTimer, "query")
            }
            Session
        }
        .exec { Session ->
            val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
            timer = CustomMetrics.findAssetsByAccountIdQueryTimer.labels(
                "gatling",
                System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                TransferAssetsQuery::class.simpleName
            ).startTimer()
            CustomMetrics.findAssetsByAccountIdQueryCount.labels(
                "gatling",
                System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                TransferAssetsQuery::class.simpleName
            ).inc()
            sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryCount, "query")
            try {
                runBlocking {
                    QueryBuilder.findAssetsByAccountId(targetDevAccountIdReceiver)
                        .account(targetDevAccountIdReceiver)
                        .buildSigned(anotherDevKeyPair)
                        .let { query ->
                            iroha2Client.sendQuery(query)
                        }
                }
            } catch (ex: RuntimeException) {
                CustomMetrics.findAssetsByAccountIdQueryErrorCount.labels(
                    "gatling",
                    System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                    TransferAssetsQuery::class.simpleName
                ).inc()
                sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryErrorCount, "query")
                println("Something went wrong on findAssetsByAccountIdQuery request, problem with find asset query: " + ex.message)
                pliers.healthCheck(false, "TransferAssets")
            } finally {
                timer.observeDuration()
                CustomMetrics.findAssetsByAccountIdQueryCount.labels(
                    "gatling",
                    System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                    TransferAssetsQuery::class.simpleName
                ).inc()
                sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryCount, "query")
                sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryTimer, "query")
            }
            attempt++
            if (attempt % 10 == 0) {
                val newSession = Session.set("condition", true)
                newSession
            } else {
                val newSession = Session.set("condition", false)
                newSession
            }
        }.doIf { Session -> Session.getBoolean("condition") }
        .then(
            exec { Session ->
                val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
                timer = CustomMetrics.subscriptionToBlockStreamTimer.labels(
                    "gatling",
                    System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                    TransferAssetsQuery::class.simpleName
                ).startTimer()
                try {
                    val idToSubscription: Pair<Iterable<BlockStreamStorage>, BlockStreamSubscription> =
                        iroha2Client.subscribeToBlockStream(1, 2)
                    subscription = idToSubscription.component2()
                } finally {
                    timer.observeDuration()
                    CustomMetrics.subscriptionToBlockStreamCount.labels(
                        "gatling",
                        System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                        TransferAssetsQuery::class.simpleName
                    ).inc()
                    sendMetricsToPrometheus(CustomMetrics.subscriptionToBlockStreamCount, "transaction")
                    sendMetricsToPrometheus(CustomMetrics.subscriptionToBlockStreamTimer, "transaction")
                }
                timer = CustomMetrics.transferAssetTimer.labels(
                    "gatling",
                    System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                    TransferAssetsQuery::class.simpleName
                ).startTimer()
                CustomMetrics.transferAssetCount.labels(
                    "gatling",
                    System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                    TransferAssetsQuery::class.simpleName
                ).inc()
                sendMetricsToPrometheus(CustomMetrics.transferAssetCount, "transaction")
                try {
                    runBlocking {
                        iroha2Client.sendTransaction {
                            account(anotherDevAccountIdSender)
                            transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                            buildSigned(anotherDevKeyPair)
                        }.also { d ->
                            withTimeout(Duration.ofSeconds(transactionWaiter)) {
                                d.await()
                                pliers.healthCheck(true, "TransferAssets")
                            }
                        }
                        subscription.close()
                    }
                } catch (ex: RuntimeException) {
                    CustomMetrics.transferAssetErrorCount.labels(
                        "gatling",
                        System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                        TransferAssetsQuery::class.simpleName
                    ).inc()
                    sendMetricsToPrometheus(CustomMetrics.transferAssetErrorCount, "transaction")
                    println("Something went wrong on TransferAssets scenario, problem with transfer asset transaction: " + ex.message)
                    pliers.healthCheck(false, "TransferAssets")
                } finally {
                    timer.observeDuration()
                    sendMetricsToPrometheus(CustomMetrics.transferAssetTimer, "transaction")
                }
                val newSession = Session.set("condition", false)
                newSession
            }
                .exec { Session ->
                    val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
                    timer = CustomMetrics.findAssetsByAccountIdQueryTimer.labels(
                        "gatling",
                        System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                        TransferAssetsQuery::class.simpleName
                    ).startTimer()
                    CustomMetrics.findAssetsByAccountIdQueryCount.labels(
                        "gatling",
                        System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                        TransferAssetsQuery::class.simpleName
                    ).inc()
                    sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryCount, "query")
                    try {
                        runBlocking {
                            QueryBuilder.findAssetsByAccountId(anotherDevAccountIdSender)
                                .account(anotherDevAccountIdSender)
                                .buildSigned(anotherDevKeyPair)
                                .let { query ->
                                    iroha2Client.sendQuery(query)
                                }
                        }
                    } catch (ex: RuntimeException) {
                        CustomMetrics.findAssetsByAccountIdQueryErrorCount.labels(
                            "gatling",
                            System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                            TransferAssetsQuery::class.simpleName
                        ).inc()
                        sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryErrorCount, "query")
                        println("Something went wrong on findAssetsByAccountIdQuery request, problem with find asset query: " + ex.message)
                        pliers.healthCheck(false, "TransferAssets")
                    } finally {
                        timer.observeDuration()
                        CustomMetrics.findAssetsByAccountIdQueryCount.labels(
                            "gatling",
                            System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                            TransferAssetsQuery::class.simpleName
                        ).inc()
                        sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryCount, "query")
                        sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryTimer, "query")
                    }
                    Session
                }
                .exec { Session ->
                    val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
                    timer = CustomMetrics.findAssetsByAccountIdQueryTimer.labels(
                        "gatling",
                        System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                        TransferAssetsQuery::class.simpleName
                    ).startTimer()
                    CustomMetrics.findAssetsByAccountIdQueryCount.labels(
                        "gatling",
                        System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                        TransferAssetsQuery::class.simpleName
                    ).inc()
                    sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryCount, "query")
                    try {
                        runBlocking {
                            QueryBuilder.findAssetsByAccountId(targetDevAccountIdReceiver)
                                .account(targetDevAccountIdReceiver)
                                .buildSigned(anotherDevKeyPair)
                                .let { query ->
                                    iroha2Client.sendQuery(query)
                                }
                        }
                    } catch (ex: RuntimeException) {
                        CustomMetrics.findAssetsByAccountIdQueryErrorCount.labels(
                            "gatling",
                            System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                            TransferAssetsQuery::class.simpleName
                        ).inc()
                        sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryErrorCount, "query")
                        println("Something went wrong on findAssetsByAccountIdQuery request, problem with find asset query: " + ex.message)
                        pliers.healthCheck(false, "TransferAssets")
                    } finally {
                        timer.observeDuration()
                        CustomMetrics.findAssetsByAccountIdQueryCount.labels(
                            "gatling",
                            System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                            TransferAssetsQuery::class.simpleName
                        ).inc()
                        sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryCount, "query")
                        sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryTimer, "query")
                    }
                    Session
                }
        )

}
