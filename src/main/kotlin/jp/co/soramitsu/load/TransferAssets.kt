package jp.co.soramitsu.load

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.ScenarioBuilder
import io.prometheus.client.Histogram
import jp.co.soramitsu.iroha2.asAccountId
import jp.co.soramitsu.iroha2.asAssetId
import jp.co.soramitsu.iroha2.generated.*
import jp.co.soramitsu.iroha2.asDomainId
import jp.co.soramitsu.iroha2.client.Iroha2Client
import jp.co.soramitsu.load.TechicalScns.Iroha2SetUp
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig
import jp.co.soramitsu.load.objects.CustomMetrics
import jp.co.soramitsu.load.toolbox.Wrench13
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.withTimeout
import java.security.KeyPair
import java.time.Duration

class TransferAssets : Wrench13() {
    lateinit var domainIdSender: DomainId
    lateinit var anotherDevAccountIdSender: AccountId
    lateinit var anotherDevAssetIdSender: AssetId
    lateinit var targetDevAccountIdReceiver: AccountId
    lateinit var anotherDevKeyPairSender: KeyPair
    //lateinit var iroha2Client: Iroha2Client

    companion object {
        @JvmStatic
        fun apply() = runBlocking {
            TransferAssets().applyScn()
        }
    }

    fun applyScn(): ScenarioBuilder {
        return transferAssetsScn
    }

    val transferAssetsScn = scenario("TransferAssets")
        .feed(csv("preconditionList.csv").circular())
        .exec { Session ->
            //iroha2Client = buildClient(SimulationConfig.simulation.configuration())
            anotherDevKeyPairSender = adminKeyPair
            domainIdSender = Session.get<String>("domainIdSender")!!.asDomainId()
            anotherDevAccountIdSender = Session.get<String>("anotherDevAccountIdSender")!!.asAccountId()
            targetDevAccountIdReceiver = Session.get<String>("anotherDevAccountIdReceiver")!!.asAccountId()
            anotherDevAssetIdSender = Session.get<String>("anotherDevAssetIdSender")!!.asAssetId()
            Session
        }
        .exec { Session ->
            runBlocking {
                val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
                iroha2Client.fireAndForget {
                    account(anotherDevAccountIdSender)
                    transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                    buildSigned(anotherDevKeyPairSender)
                }
            }

            Session
        }.exec { Session ->
            runBlocking {
                val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
                iroha2Client.fireAndForget {
                    account(anotherDevAccountIdSender)
                    transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                    buildSigned(anotherDevKeyPairSender)
                }
            }
            Session
        }.exec { Session ->
            runBlocking {
                val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
                iroha2Client.fireAndForget {
                    account(anotherDevAccountIdSender)
                    transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                    buildSigned(anotherDevKeyPairSender)
                }
            }
            Session
        }.exec { Session ->
            runBlocking {
                val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
                iroha2Client.fireAndForget {
                    account(anotherDevAccountIdSender)
                    transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                    buildSigned(anotherDevKeyPairSender)
                }
            }
            Session
        }.exec { Session ->
            runBlocking {
                val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
                iroha2Client.fireAndForget {
                    account(anotherDevAccountIdSender)
                    transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                    buildSigned(anotherDevKeyPairSender)
                }
            }
            Session
        }.exec { Session ->
            runBlocking {
                val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
                iroha2Client.fireAndForget {
                    account(anotherDevAccountIdSender)
                    transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                    buildSigned(anotherDevKeyPairSender)
                }
            }
            Session
        }.exec { Session ->
            runBlocking {
                val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
                iroha2Client.fireAndForget {
                    account(anotherDevAccountIdSender)
                    transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                    buildSigned(anotherDevKeyPairSender)
                }
            }
            Session
        }.exec { Session ->
            runBlocking {
                val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
                iroha2Client.fireAndForget {
                    account(anotherDevAccountIdSender)
                    transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                    buildSigned(anotherDevKeyPairSender)
                }
            }
            Session
        }.exec { Session ->
            runBlocking {
                val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
                iroha2Client.fireAndForget {
                    account(anotherDevAccountIdSender)
                    transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                    buildSigned(anotherDevKeyPairSender)
                }
            }
            Session
        }.exec { Session ->
            runBlocking {
                val iroha2Client = buildClient(SimulationConfig.simulation.configuration())
                iroha2Client.fireAndForget {
                    account(anotherDevAccountIdSender)
                    transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                    buildSigned(anotherDevKeyPairSender)
                }
            }
            Session
        }
}