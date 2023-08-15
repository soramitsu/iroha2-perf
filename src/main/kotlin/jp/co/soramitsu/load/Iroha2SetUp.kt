package jp.co.soramitsu.load

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.ScenarioBuilder
import io.gatling.javaapi.core.Session
import jp.co.soramitsu.iroha2.*
import jp.co.soramitsu.iroha2.generated.*
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig
import jp.co.soramitsu.load.objects.AnotherDev
import jp.co.soramitsu.load.objects.AnotherDevs
import jp.co.soramitsu.load.toolbox.BlueElectricalTape
import jp.co.soramitsu.load.toolbox.Wrench13
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.withTimeout
import java.time.Duration
import java.util.*

class Iroha2SetUp : Wrench13() {
    companion object {
        @JvmStatic
        fun apply() = runBlocking {
            Iroha2SetUp().applyScn()
        }
    }

    fun applyScn(): ScenarioBuilder {
        return iroha2SetUpScn
    }


    val iroha2SetUpScn = scenario("Iroha2SetUp")
        .exec { Session ->
            listener = BlueElectricalTape()
            Session
        }
        .exec { Session ->
            listener.metricRegistry.timer("subscription_to_block_stream").time(
                Runnable {
                    pliers.getSubscribetionToBlockStream(Iroha2Client, 1, 2)
                }
            )
            anotherDevDomainId = "bulb_${UUID.randomUUID()}_${UUID.randomUUID()}".asDomainId()
            anotherDevDomainIdList.add(anotherDevDomainId)
            println("START transaction_domain_register")
            listener.metricRegistry.timer("transaction_domain_register").time(
                Runnable {
                    runBlocking {
                        Iroha2Client.sendTransaction {
                            account(admin)
                            registerDomain(anotherDevDomainId)
                            buildSigned(adminKeyPair)
                        }.also { d ->
                            withTimeout(Duration.ofSeconds(transactionWaiter)) { d.await() }
                        }
                    }
                }
            )
            println("END transaction_domain_register")
            Thread.sleep(4000)
            println("START getLastDomain()")
            /*runBlocking {
                pliers.getLastDomain()
            }*/
            println("END getLastDomain()")
            val newSession: Session = Session.set("committed", listener.transaction.commit)
            newSession
        }
        .doIf { Session -> Session.get<Boolean>("committed") }
        .then(
            foreach(anotherDevDomainIdList, "domainId").on(
                repeat(SimulationConfig.simulation.usersForSetUp).on(
                    exec { Session ->
                        listener.metricRegistry.timer("subscription_to_block_stream").time(
                            Runnable {
                                pliers.getSubscribetionToBlockStream(Iroha2Client, 1, 2)
                            }
                        )
                        var anotherDev = AnotherDev()
                        anotherDev.anotherDevDomainId = Session.get<DomainId>("domainId")!!
                        anotherDev.anotherDevAccountId = AccountId(
                            Name("anotherDev${UUID.randomUUID()}_${UUID.randomUUID()}"),
                            anotherDev.anotherDevDomainId
                        )
                        anotherDev.anotherDevKeyPair = generateKeyPair()
                        println("transaction_account_register")
                        listener.metricRegistry.timer("transaction_account_register").time(
                            Runnable {
                                runBlocking {
                                    Iroha2Client.sendTransaction {
                                        account(admin)
                                        registerAccount(
                                            anotherDev.anotherDevAccountId,
                                            listOf(anotherDev.anotherDevKeyPair.public.toIrohaPublicKey())
                                        )
                                        buildSigned(adminKeyPair)
                                    }.also { d ->
                                        withTimeout(Duration.ofSeconds(transactionWaiter)) { d.await() }
                                    }
                                }
                            }
                        )
                        Thread.sleep(4000)
                        println("END transaction_account_register")
                        /*runBlocking {
                            pliers.getLastDomain()
                        }*/
                        listener.metricRegistry.timer("subscription_to_block_stream").time(
                            Runnable {
                                pliers.getSubscribetionToBlockStream(Iroha2Client, 1, 2)
                            }
                        )
                        anotherDev.assetDefinitionId = AssetDefinitionId(
                            "xor${UUID.randomUUID()}_${UUID.randomUUID()}".asName(),
                            anotherDev.anotherDevDomainId
                        )
                        println("transaction_asset_definition_register")
                        listener.metricRegistry.timer("transaction_asset_definition_register").time(
                            Runnable {
                                runBlocking {
                                    Iroha2Client.sendTransaction {
                                        account(anotherDev.anotherDevAccountId)
                                        registerAssetDefinition(anotherDev.assetDefinitionId, AssetValueType.Quantity())
                                        buildSigned(anotherDev.anotherDevKeyPair)
                                    }.also { d ->
                                        withTimeout(Duration.ofSeconds(transactionWaiter)) { d.await() }
                                    }
                                }
                            }
                        )
                        Thread.sleep(4000)
                        println("END transaction_asset_definition_register")
                        /*runBlocking {
                            pliers.getLastDomain()
                        }*/
                        listener.metricRegistry.timer("subscription_to_block_stream").time(
                            Runnable {
                                pliers.getSubscribetionToBlockStream(Iroha2Client, 1, 2)
                            }
                        )
                        anotherDev.anotherDevAssetId =
                            AssetId(anotherDev.assetDefinitionId, anotherDev.anotherDevAccountId)
                        println("transaction_mint_asset")
                        listener.metricRegistry.timer("transaction_mint_asset").time(
                            Runnable {
                                runBlocking {
                                    Iroha2Client.sendTransaction {
                                        account(anotherDev.anotherDevAccountId)
                                        mintAsset(anotherDev.anotherDevAssetId, 100)
                                        buildSigned(anotherDev.anotherDevKeyPair)
                                    }.also { d ->
                                        withTimeout(Duration.ofSeconds(transactionWaiter)) { d.await() }
                                    }
                                }
                            }
                        )
                        Thread.sleep(4000)
                        /*runBlocking {
                            pliers.getLastDomain()
                        }*/

                        println("END transaction_mint_asset")
                        AnotherDevs.list.add(anotherDev)
                        Session
                    }
                )
            )
        )
}