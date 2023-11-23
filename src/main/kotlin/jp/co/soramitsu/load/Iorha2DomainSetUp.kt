package jp.co.soramitsu.load

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.ScenarioBuilder
import jp.co.soramitsu.iroha2.asDomainId
import jp.co.soramitsu.iroha2.client.blockstream.BlockStreamStorage
import jp.co.soramitsu.iroha2.client.blockstream.BlockStreamSubscription
import jp.co.soramitsu.load.objects.CustomHistogram
import jp.co.soramitsu.load.toolbox.Wrench13
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.withTimeout
import java.time.Duration
import java.util.*

class Iorha2DomainSetUp  : Wrench13(){
    companion object {
        @JvmStatic
        fun apply() = runBlocking {
            Iorha2DomainSetUp().applyScn()
        }
    }

    fun applyScn(): ScenarioBuilder {
        return iorha2DomainSetUp
    }

    val iorha2DomainSetUp = CoreDsl.scenario("iorha2DomainSetUp")
        .exec { Session ->
            val iroha2Client = buildClient()

            timer = CustomHistogram.subscriptionToBlockStreamTimer.labels(
                "gatling"
                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                , Iroha2SetUp::class.simpleName).startTimer()
            try {
                val idToSubscription: Pair<Iterable<BlockStreamStorage>, BlockStreamSubscription> = iroha2Client.subscribeToBlockStream(1, 2)
                subscription = idToSubscription.component2()
            } finally {
                timer.observeDuration()
                CustomHistogram.subscriptionToBlockStreamCount.labels(
                    "gatling"
                    , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                    , Iroha2SetUp::class.simpleName).inc()
                sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStreamCount, "transaction")
                sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStreamTimer, "transaction")
            }
            val anotherDevDomainId = "bulb_${UUID.randomUUID()}_${UUID.randomUUID()}".asDomainId()
            timer = CustomHistogram.domainRegisterTimer.labels(
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
                            println("SEND_TRANSACTION: DOMAIN REGISTER")
                            pliers.healthCheck(true, "Iroha2SetUp")
                            anotherDevDomainIdList.add(anotherDevDomainId)
                            timer.observeDuration()
                            CustomHistogram.domainRegisterCount.labels(
                                "gatling"
                                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                , Iroha2SetUp::class.simpleName).inc()
                            sendMetricsToPrometheus(CustomHistogram.domainRegisterCount, "transaction")
                            sendMetricsToPrometheus(CustomHistogram.domainRegisterTimer, "transaction")
                        }
                    }
                }
                subscription.close()
            } catch (ex: RuntimeException) {
                println("Something went wrong on Iroha2SetUp scenario, problem with domain register transaction: " + ex.message)
                println("Something went wrong on Iroha2SetUp scenario, problem with domain register transaction: " + ex.stackTrace)
                pliers.healthCheck(false, "Iroha2SetUp")
            }
            Session
        }
}