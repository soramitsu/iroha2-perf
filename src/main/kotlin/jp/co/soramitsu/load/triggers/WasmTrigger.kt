package jp.co.soramitsu.load.triggers

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.ScenarioBuilder
import jp.co.soramitsu.iroha2.*
import jp.co.soramitsu.iroha2.generated.AccountId
import jp.co.soramitsu.iroha2.generated.Name
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

    val wasmTrigger = CoreDsl.scenario("TriggerWasm")
        .feed(CoreDsl.csv("preconditionList.csv").circular())
        .exec { Session ->
            val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
            val anotherDev = AnotherDev()
            anotherDev.anotherDevDomainId = Session.get<String>("domainIdSender")!!.asDomainId()
            anotherDev.anotherDevAccountId = AccountId(
                anotherDev.anotherDevDomainId,
                Name("anotherDev${UUID.randomUUID()}_${UUID.randomUUID()}")
            )
            anotherDev.anotherDevKeyPair = generateKeyPair()
            timer = CustomMetrics.accountRegisterTimer.labels(
                "gatling"
                , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                , WasmTrigger::class.simpleName).startTimer()
            try {
                runBlocking {
                    iroha2Client.sendTransaction {
                        account(aliceAccountId)
                        registerAccount(
                            anotherDev.anotherDevAccountId,
                            listOf(anotherDev.anotherDevKeyPair.public.toIrohaPublicKey())
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
                    subscription.close()
                }
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
            Session
        }
}