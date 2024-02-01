package jp.co.soramitsu.load.triggers

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.ScenarioBuilder
import jp.co.soramitsu.iroha2.*
import jp.co.soramitsu.iroha2.generated.*
import jp.co.soramitsu.iroha2.query.QueryBuilder
import jp.co.soramitsu.load.TechicalScns.Iroha2SetUp
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig
import jp.co.soramitsu.load.objects.AnotherDev
import jp.co.soramitsu.load.objects.CustomMetrics
import jp.co.soramitsu.load.toolbox.Wrench13
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.withTimeout
import java.time.Duration

import java.util.*

class WasmTrigger: Wrench13() {
    companion object {
        @JvmStatic
        fun apply() = runBlocking {
            WasmTrigger().applyScn()
        }
    }

    fun applyScn(): ScenarioBuilder {
        return wasmTrigger
    }

    val anotherDev = AnotherDev()

    val wasmTrigger = CoreDsl.scenario("WasmTrigger")
        .feed(CoreDsl.csv("preconditionList.csv").circular())
        .exec { Session ->
            runBlocking{
                val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
                anotherDev.anotherDevDomainId = Session.get<String>("domainIdSender")!!.asDomainId()
                anotherDev.anotherDevAccountId = AccountId(
                    anotherDev.anotherDevDomainId,
                    Name("anotherDev${UUID.randomUUID()}_${UUID.randomUUID()}")
                )
                anotherDev.anotherDevKeyPair = generateKeyPair()
                val triggerKey = "mintAsset".asName()
                val triggerValue = "i_wont_a_cat".asValue()
                val metadata = Metadata(
                    mapOf(
                        Pair(triggerKey, triggerValue),
                    ),
                )
                timer = CustomMetrics.subscriptionToBlockStreamTimer.labels(
                    "gatling",
                    System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                    Iroha2SetUp::class.simpleName
                ).startTimer()
                val idToSubscription = iroha2Client.subscribeToBlockStream(1, 2)
                val subscription = idToSubscription.second
                timer.observeDuration()
                CustomMetrics.subscriptionToBlockStreamCount.labels(
                    "gatling",
                    System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\"),
                    Iroha2SetUp::class.simpleName
                ).inc()
                sendMetricsToPrometheus(CustomMetrics.subscriptionToBlockStreamCount, "transaction")
                sendMetricsToPrometheus(CustomMetrics.subscriptionToBlockStreamTimer, "transaction")
                timer = CustomMetrics.accountRegisterTimer.labels(
                    "gatling"
                    , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                    , WasmTrigger::class.simpleName).startTimer()
                try {
                     iroha2Client.sendTransaction {
                         account(aliceAccountId)
                         registerAccount(
                             anotherDev.anotherDevAccountId,
                             listOf(anotherDev.anotherDevKeyPair.public.toIrohaPublicKey()),
                             metadata = metadata
                         )
                         buildSigned(adminKeyPair)
                     }.also { d ->
                         withTimeout(Duration.ofSeconds(transactionWaiter)) {
                             d.await()
                             CustomMetrics.accountRegisterCount.labels(
                                 "gatling"
                                 , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                 , WasmTrigger::class.simpleName).inc()
                             sendMetricsToPrometheus(CustomMetrics.accountRegisterCount, "transaction")
                         }
                     }
                     subscription.stop()
                } catch (ex: RuntimeException) {
                    CustomMetrics.accountRegisterErrorCount.labels(
                        "gatling"
                        , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                        , WasmTrigger::class.simpleName).inc()
                    sendMetricsToPrometheus(CustomMetrics.accountRegisterErrorCount, "transaction")
                    println(ex.message)
                } finally {
                    timer.observeDuration()
                    sendMetricsToPrometheus(CustomMetrics.accountRegisterTimer, "transaction")
                }
            }
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
                    QueryBuilder.findAssetsByAccountId(accountId = anotherDev.anotherDevAccountId)
                        .account(anotherDev.anotherDevAccountId)
                        .buildSigned(anotherDev.anotherDevKeyPair)
                        .let { query ->
                            iroha2Client.sendQuery(query)
                        }.also{ assets ->
                            if(assets.last().id.definitionId.name.string != "cat"){
                                timer.observeDuration()
                                CustomMetrics.findAssetsByAccountIdQueryCount.labels(
                                    "gatling"
                                    , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                                    , WasmTrigger::class.simpleName).inc()
                                sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryErrorCount, "query")
                            }

                        }
                }
            } finally {
                timer.observeDuration()
                CustomMetrics.findAssetsByAccountIdQueryCount.labels(
                    "gatling"
                    , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                    , WasmTrigger::class.simpleName).inc()
                sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryCount, "query")
                sendMetricsToPrometheus(CustomMetrics.findAssetsByAccountIdQueryTimer, "query")
            }
            Thread.sleep(queryWaiter)
            Session
        }
}