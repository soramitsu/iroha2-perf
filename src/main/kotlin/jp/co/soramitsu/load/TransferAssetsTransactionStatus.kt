
package jp.co.soramitsu.load

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.CoreDsl.exec
import io.gatling.javaapi.core.ScenarioBuilder
import jp.co.soramitsu.iroha2.client.Iroha2Client
import jp.co.soramitsu.iroha2.query.QueryBuilder
import jp.co.soramitsu.load.objects.AnotherDevs
import jp.co.soramitsu.load.objects.CustomHistogram
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
    val randomIndex = (0 until peers.size).random()
    val randomPeer = peers[randomIndex]
    val Iroha2Client: Iroha2Client = buildClient(randomPeer)

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
                timer = CustomHistogram.findAssetsByAccountIdQueryTimer.labels(
                    "gatling"
                    , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                    , Iroha2SetUp::class.simpleName).startTimer()
                try {
                    runBlocking {
                        QueryBuilder.findAssetsByAccountId(accountId = currentDevAccountId)
                            .account(currentDevAccountId)
                            .buildSigned(currentDevKeyPair)
                            .let { query ->
                                Iroha2Client.sendQuery(query)
                            }.let {
                                currentDevAssetId = it.get(0).id
                            }
                    }
                } finally {
                    timer.observeDuration()
                    CustomHistogram.findAssetsByAccountIdQueryCount.labels(
                        "gatling"
                        , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                        , Iroha2SetUp::class.simpleName).inc()
                    //sendMetricsToPrometheus(CustomHistogram.findAssetsByAccountIdQueryCount, "query")
                    //sendMetricsToPrometheus(CustomHistogram.findAssetsByAccountIdQueryTimer, "query")
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
                        timer = CustomHistogram.subscriptionToBlockStreamTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName
                        ).startTimer()
                        try {
                            Iroha2Client.subscribeToBlockStream(1,2,true)
                        } finally {
                            timer.observeDuration()
                            CustomHistogram.subscriptionToBlockStreamCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            //sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStreamCount, "transaction")
                            //sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStreamTimer, "transaction")
                        }
                        timer = CustomHistogram.transferAssetTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName
                        ).startTimer()
                        try {
                            runBlocking {
                                Iroha2Client.sendTransaction {
                                    account(currentDevAccountId)
                                    transferAsset(currentDevAssetId, 10, targetDevAccountId)
                                    buildSigned(currentDevKeyPair)
                                }.also { d ->
                                    withTimeout(Duration.ofSeconds(transactionWaiter)) { d.await() }
                                }
                            }
                        } finally {
                            timer.observeDuration()
                            CustomHistogram.transferAssetCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            //sendMetricsToPrometheus(CustomHistogram.transferAssetCount, "transaction")
                            //sendMetricsToPrometheus(CustomHistogram.transferAssetTimer, "transaction")
                        }
                        val newSession = Session.set("condition", false)
                        newSession
                    }
                )
        )
}

