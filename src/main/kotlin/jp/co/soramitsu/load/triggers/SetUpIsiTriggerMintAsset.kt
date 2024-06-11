package jp.co.soramitsu.load.triggers

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.ScenarioBuilder
import jp.co.soramitsu.iroha2.asDomainId
import jp.co.soramitsu.iroha2.asName
import jp.co.soramitsu.iroha2.generated.*
//import jp.co.soramitsu.iroha2.numeric
import jp.co.soramitsu.iroha2.transaction.Instructions
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig
import jp.co.soramitsu.load.toolbox.Wrench13
import kotlinx.coroutines.runBlocking
import java.security.KeyPair
import java.util.*

class SetUpIsiTriggerMintAsset : Wrench13() {
    companion object {
        @JvmStatic
        fun apply() = runBlocking {
            SetUpIsiTriggerMintAsset().applyScn()
        }
    }

    fun applyScn(): ScenarioBuilder {
        return setUp
    }

    val setUp = CoreDsl.scenario("SetUpIsiTrigger")
        .exec { Session ->
            val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
                val chainId = UUID.fromString(  "00000000-0000-0000-0000-000000000000")
            val triggerId = TriggerId(name = "executable_trigger".asName())
            val devAccountId = AccountId("bulb${UUID.randomUUID()}_${UUID.randomUUID()}".asDomainId(),
                Name("anotherDev${UUID.randomUUID()}_${UUID.randomUUID()}"))
            val assetDefinitionId = AssetDefinitionId(
                "wonderland".asDomainId(),
                "xor${UUID.randomUUID()}_${UUID.randomUUID()}".asName()

            )
            val assetId = AssetId(assetDefinitionId, devAccountId)
            runBlocking {
                iroha2Client.sendTransaction {
                    accountId = aliceAccountId
                    /*registerExecutableTrigger(
                        triggerId,
                        listOf(
                            Instructions.registerAssetDefinition(
                                assetDefinitionId,
                                AssetValueType.numeric()
                            ),
                            Instructions.mintAsset(assetId, 1)
                        ),
                        Repeats.Indefinitely(),
                        aliceAccountId
                    )*/
                    executeTrigger(triggerId)
                    buildSigned(adminKeyPair)
                }
            }
            Session
        }

}