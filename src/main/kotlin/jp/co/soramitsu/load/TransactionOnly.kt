package jp.co.soramitsu.load

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.ScenarioBuilder
import jp.co.soramitsu.iroha2.client.blockstream.BlockStreamStorage
import jp.co.soramitsu.iroha2.client.blockstream.BlockStreamSubscription
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig
import jp.co.soramitsu.load.objects.AnotherDevs
import jp.co.soramitsu.load.objects.CustomMetrics
import jp.co.soramitsu.load.toolbox.Wrench13
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.withTimeout
import java.time.Duration
import kotlin.random.Random

class TransactionOnly: Wrench13() {
    companion object {
        @JvmStatic
        fun apply() = runBlocking {
            TransactionOnly().applyScn()
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
                    val iroha2Client = buildClient()
                    timer = CustomMetrics.subscriptionToBlockStreamTimer.labels(
                        "gatling",
                        System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                        Iroha2SetUp::class.simpleName
                    ).startTimer()
                    try {
                        val idToSubscription: Pair<Iterable<BlockStreamStorage>, BlockStreamSubscription> = iroha2Client.subscribeToBlockStream(1, 2)
                        subscription = idToSubscription.component2()
                    } finally {
                        timer.observeDuration()
                        CustomMetrics.subscriptionToBlockStreamCount.labels(
                            "gatling",
                            System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                            Iroha2SetUp::class.simpleName
                        ).inc()
                        sendMetricsToPrometheus(CustomMetrics.subscriptionToBlockStreamCount, "transaction")
                        sendMetricsToPrometheus(CustomMetrics.subscriptionToBlockStreamTimer, "transaction")
                    }
                    timer = CustomMetrics.transferAssetTimer.labels(
                        "gatling",
                        System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                        Iroha2SetUp::class.simpleName
                    ).startTimer()
                    //1 засунуть сюда каунтер который будт отображать попытку отправки транзакции
                    CustomMetrics.transferAssetCount.labels(
                        "gatling",
                        System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                        Iroha2SetUp::class.simpleName
                    ).inc()
                    sendMetricsToPrometheus(CustomMetrics.transferAssetCount, "transaction")
                    try {
                        println("SEND_TRANSACTION: TRANSFER ASSET")
                        runBlocking {
                            iroha2Client.sendTransaction {
                                account(currentDevAccountId)
                                transferAsset(currentDevAssetId, 1, targetDevAccountId)
                                buildSigned(currentDevKeyPair)
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
                            Iroha2SetUp::class.simpleName
                        ).inc()
                        sendMetricsToPrometheus(CustomMetrics.transferAssetErrorCount, "transaction")
                        println("Something went wrong on TransferAssets scenario, problem with transfer asset transaction: " + ex.message)
                        println("Something went wrong on TransferAssets scenario, problem with transfer asset transaction: " + ex.stackTrace)
                        pliers.healthCheck(false, "TransferAssets")
                    } finally {
                        timer.observeDuration()
                        sendMetricsToPrometheus(CustomMetrics.transferAssetTimer, "transaction")
                    }
                    val newSession = Session.set("condition", false)
                    newSession
                }

        )
}