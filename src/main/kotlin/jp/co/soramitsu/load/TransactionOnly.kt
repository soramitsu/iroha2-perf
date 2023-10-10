package jp.co.soramitsu.load

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.ScenarioBuilder
import jp.co.soramitsu.iroha2.query.QueryBuilder
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig
import jp.co.soramitsu.load.objects.AnotherDevs
import jp.co.soramitsu.load.objects.CustomHistogram
import jp.co.soramitsu.load.toolbox.Wrench13
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.withTimeout
import java.time.Duration
import kotlin.random.Random

class TransactionOnly: Wrench13() {
    companion object {
        @JvmStatic
        fun apply() = runBlocking {
            TransferAssetsTransactionStatus().applyScn()
        }
    }

    fun applyScn(): ScenarioBuilder {
        return transferAssetsOnlyScn
    }

    val transferAssetsOnlyScn = CoreDsl.scenario("TransferAssets")
        .repeat(SimulationConfig.simulation.attemptsToTransaction)
        .on(
            CoreDsl.exec { Session ->
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
                        timer = CustomHistogram.subscriptionToBlockStreamTimer.labels(
                            "gatling",
                            System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                            Iroha2SetUp::class.simpleName
                        ).startTimer()
                        try {
                            Iroha2Client.subscribeToBlockStream(1, 2, true)
                        } finally {
                            timer.observeDuration()
                            CustomHistogram.subscriptionToBlockStreamCount.labels(
                                "gatling",
                                System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                                Iroha2SetUp::class.simpleName
                            ).inc()
                            sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStreamCount, "transaction")
                            sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStreamTimer, "transaction")
                        }
                        timer = CustomHistogram.transferAssetTimer.labels(
                            "gatling",
                            System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                            Iroha2SetUp::class.simpleName
                        ).startTimer()
                        try {
                            println("SEND_TRANSACTION: TRANSFER ASSET")
                            runBlocking {
                                Iroha2Client.sendTransaction {
                                    account(currentDevAccountId)
                                    transferAsset(currentDevAssetId, 1, targetDevAccountId)
                                    buildSigned(currentDevKeyPair)
                                }.also { d ->
                                    withTimeout(Duration.ofSeconds(transactionWaiter)) { d.await() }
                                }
                            }
                        } finally {
                            timer.observeDuration()
                            CustomHistogram.transferAssetCount.labels(
                                "gatling",
                                System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                                Iroha2SetUp::class.simpleName
                            ).inc()
                            sendMetricsToPrometheus(CustomHistogram.transferAssetCount, "transaction")
                            sendMetricsToPrometheus(CustomHistogram.transferAssetTimer, "transaction")
                        }
                        val newSession = Session.set("condition", false)
                        newSession
                    }
        )
}