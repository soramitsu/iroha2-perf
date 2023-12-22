package jp.co.soramitsu.load

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.ScenarioBuilder
import jp.co.soramitsu.iroha2.asAccountId
import jp.co.soramitsu.iroha2.asAssetId
import jp.co.soramitsu.iroha2.client.Iroha2Client
import jp.co.soramitsu.iroha2.client.blockstream.BlockStreamStorage
import jp.co.soramitsu.iroha2.client.blockstream.BlockStreamSubscription
import jp.co.soramitsu.iroha2.generated.*
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig
import jp.co.soramitsu.iroha2.asDomainId
import jp.co.soramitsu.load.objects.CustomMetrics
import jp.co.soramitsu.load.toolbox.Wrench13
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.withTimeout
import java.security.KeyPair
import java.time.Duration

class TransactionOnlyFeeder : Wrench13() {

    lateinit var domainIdSender: DomainId
    lateinit var anotherDevAccountIdSender: AccountId
    lateinit var anotherDevAssetIdSender: AssetId
    lateinit var targetDevAccountIdReceiver: AccountId
    lateinit var anotherDevKeyPairSender: KeyPair

    companion object {
        @JvmStatic
        fun apply() = runBlocking {
            TransactionOnlyFeeder().applyScn()
        }
    }

    fun applyScn(): ScenarioBuilder {
        return transferAssetsOnlyScn
    }

    val transferAssetsOnlyScn = scenario("TransferAssets")
        .feed(csv("preconditionList.csv").circular())
        .exec { Session ->
            anotherDevKeyPairSender = adminKeyPair
            domainIdSender = Session.get<String>("domainIdSender")!!.asDomainId()
            anotherDevAccountIdSender = Session.get<String>("anotherDevAccountIdSender")!!.asAccountId()
            targetDevAccountIdReceiver = Session.get<String>("anotherDevAccountIdReceiver")!!.asAccountId()
            anotherDevAssetIdSender = Session.get<String>("anotherDevAssetIdSender")!!.asAssetId()
            Session
        }
        .exec { Session ->
            //val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
            val iroha2Client = Iroha2Client("http://0.0.0.0:8080", "http://0.0.0.0:8180", "http://0.0.0.0:1337", true)
            timer = CustomMetrics.subscriptionToBlockStreamTimer.labels(
                "gatling",
                System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                Iroha2SetUp::class.simpleName
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
            CustomMetrics.transferAssetCount.labels(
                "gatling",
                System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                Iroha2SetUp::class.simpleName
            ).inc()
            sendMetricsToPrometheus(CustomMetrics.transferAssetCount, "transaction")
            try {
                runBlocking {
                    iroha2Client.sendTransaction {
                        account(anotherDevAccountIdSender)
                        transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                        buildSigned(anotherDevKeyPairSender)
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
                pliers.healthCheck(false, "TransferAssets")
            } finally {
                timer.observeDuration()
                sendMetricsToPrometheus(CustomMetrics.transferAssetTimer, "transaction")
            }
            val newSession = Session.set("condition", false)
            newSession
        }
}