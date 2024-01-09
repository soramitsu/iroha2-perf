package jp.co.soramitsu.load.triggers

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.ScenarioBuilder
import jp.co.soramitsu.iroha2.asDomainId
import jp.co.soramitsu.iroha2.asName
import jp.co.soramitsu.iroha2.asValue
import jp.co.soramitsu.iroha2.generated.*
import jp.co.soramitsu.iroha2.query.QueryBuilder
import jp.co.soramitsu.iroha2.transaction.EventFilters
import jp.co.soramitsu.iroha2.transaction.Instructions
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig
import jp.co.soramitsu.load.toolbox.Wrench13
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.withTimeout
import java.math.BigInteger
import java.util.*

class SetUpWasmTrigger : Wrench13() {
    companion object {
        @JvmStatic
        fun apply() = runBlocking {
            SetUpWasmTrigger().applyScn()
        }
    }

    fun applyScn(): ScenarioBuilder {
        return setUp
    }

    val setUp = CoreDsl.scenario("SetUpWasmTrigger")
        .exec { Session ->
            val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
            val triggerId = TriggerId(name = "wasm_trigger".asName())

            val currentTime = Date().time / 1000
            val filter = TriggeringFilterBox.Time(
                EventFilters.timeEventFilter(
                    Duration(BigInteger.valueOf(currentTime), 0),
                    Duration(BigInteger.valueOf(1L), 0),
                ),
            )
            val wasm = this.javaClass.classLoader
                .getResource("trigger.wasm")
                .readBytes()

            runBlocking {
                iroha2Client.sendTransaction {
                    accountId = aliceAccountId
                    registerWasmTrigger(
                        triggerId,
                        wasm,
                        Repeats.Indefinitely(),
                        aliceAccountId,
                        Metadata(mapOf()),
                        filter,
                    )
                    buildSigned(adminKeyPair)
                }.also { d ->
                    withTimeout(java.time.Duration.ofSeconds(30)) {
                        d.await()
                    }
                }
            }
            Session
        }
}