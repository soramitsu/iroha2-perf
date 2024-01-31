
package jp.co.soramitsu.load.TechicalScns

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.CoreDsl.exec
import io.gatling.javaapi.core.ScenarioBuilder
import jp.co.soramitsu.iroha2.query.QueryBuilder
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig
import jp.co.soramitsu.load.objects.AnotherDevs
import jp.co.soramitsu.load.objects.CustomMetrics
import jp.co.soramitsu.load.toolbox.Wrench13
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlinx.coroutines.time.withTimeout
import java.time.Duration

class TransferAssetsTransactionStatus: Wrench13() {
    companion object {
        @JvmStatic
        fun apply() = runBlocking {
            TransferAssetsTransactionStatus().applyScn()
        }
    }

    fun applyScn(): ScenarioBuilder {
        return transferAssetsScn
    }

    val transferAssetsScn = CoreDsl.scenario("TransferAssets")
        .repeat(userRequestCounter)
        .on(
            exec { Session ->
                var currentDevIndex: Int = Random.nextInt(0, AnotherDevs.list.size - 1)
                currentDevAccountId = AnotherDevs.list[currentDevIndex].anotherDevAccountId
                currentDevKeyPair = AnotherDevs.list[currentDevIndex].anotherDevKeyPair
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
                    , Iroha2SetUp::class.simpleName).startTimer()
                try {
                    runBlocking {
                        QueryBuilder.findAssetsByAccountId(accountId = currentDevAccountId)
                            .account(currentDevAccountId)
                            .buildSigned(currentDevKeyPair)
                            .let { query ->
                                iroha2Client.sendQuery(query)
                            }.let {
                                currentDevAssetId = it.get(0).id
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
            Thread.sleep(5000)
            attempt++
            if(attempt % attemptsPersentage == 0){
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
                            iroha2Client.subscribeToBlockStream(1,2,true)
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
                                    transferAsset(currentDevAssetId, 10, targetDevAccountId)
                                    buildSigned(currentDevKeyPair)
                                }.also { d ->
                                    withTimeout(Duration.ofSeconds(transactionWaiter)) { d.await() }
                                }
                            }
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
                )
        )
}

