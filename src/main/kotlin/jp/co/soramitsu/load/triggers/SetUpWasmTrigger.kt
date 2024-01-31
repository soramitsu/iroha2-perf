package jp.co.soramitsu.load.triggers

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.ScenarioBuilder
import jp.co.soramitsu.iroha2.asDomainId
import jp.co.soramitsu.iroha2.asName
import jp.co.soramitsu.iroha2.asValue
import jp.co.soramitsu.iroha2.generated.*
import jp.co.soramitsu.iroha2.query.QueryBuilder
import jp.co.soramitsu.iroha2.transaction.EntityFilters
import jp.co.soramitsu.iroha2.transaction.EventFilters
import jp.co.soramitsu.iroha2.transaction.Filters
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

            val filter = Filters.data(
                EntityFilters.byAccount(
                    eventFilter = AccountEventFilter.ByCreated()
                )
            )
            val wasm = this.javaClass.classLoader
                .getResource("mint_asset_for_created_user_trigger.wasm")
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