package jp.co.soramitsu.load

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.ScenarioBuilder
import jp.co.soramitsu.iroha2.client.Iroha2Client
import jp.co.soramitsu.iroha2.query.QueryBuilder
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig
import jp.co.soramitsu.load.objects.AnotherDevs
import jp.co.soramitsu.load.objects.CustomHistogram
import jp.co.soramitsu.load.toolbox.Wrench13
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.withTimeout
import org.apache.http.client.utils.URIBuilder
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
    val peers = arrayOf("peer-0/api", "peer-1/api", "peer-2/api", "peer-3/api", "peer-4/api")

    fun buildClient(peer : String): Iroha2Client {
        val peerUrl = URIBuilder().let {
            it.scheme = SimulationConfig.simulation.targetProtocol()
            it.host = SimulationConfig.simulation.targetURL()
            it.port = 0
            //it.path = SimulationConfig.simulation.targetPath()
            it.path = peer
            it.build().toURL()
        }
        urls.add(peerUrl)
        return Iroha2Client(
            urls[0],
            urls[0],
            urls[0],
            log = false,
            credentials = SimulationConfig.simulation.remoteLogin() + ":" + SimulationConfig.simulation.remotePass(),
            eventReadTimeoutInMills = 10000,
            eventReadMaxAttempts = 20
        )
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
                    val randomIndex = (0 until peers.size).random()
                    val randomPeer = peers[randomIndex]
                    val client: Iroha2Client = buildClient(randomPeer)
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
                                client.sendTransaction {
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