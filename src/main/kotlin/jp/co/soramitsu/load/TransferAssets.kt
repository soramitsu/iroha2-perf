package jp.co.soramitsu.load

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.ScenarioBuilder
import io.prometheus.client.Histogram
import jp.co.soramitsu.iroha2.*
import jp.co.soramitsu.iroha2.generated.*
import jp.co.soramitsu.iroha2.client.Iroha2Client
import jp.co.soramitsu.iroha2.query.QueryBuilder
import jp.co.soramitsu.load.TechicalScns.Iroha2SetUp
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig
import jp.co.soramitsu.load.objects.CustomMetrics
import jp.co.soramitsu.load.toolbox.Wrench13
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.withTimeout
import java.security.KeyPair
import java.time.Duration
import java.util.*

class TransferAssets : Wrench13() {
    lateinit var domainIdSender: DomainId
    lateinit var anotherDevAccountIdSender: AccountId
    lateinit var anotherDevAssetIdSender: AssetId
    lateinit var targetDevAccountIdReceiver: AccountId
    lateinit var anotherDevKeyPairSender: KeyPair
    lateinit var signature: PublicKey
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
                val chainId = UUID.fromString(  "00000000-0000-0000-0000-000000000000")
                iroha2Client.fireAndForget {
                    account(anotherDevAccountIdSender)
                    chainId(chainId)
                    transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                    buildSigned(adminKeyPair)
                }
                Thread.sleep(200)
                iroha2Client.fireAndForget {
                    account(anotherDevAccountIdSender)
                    chainId(chainId)
                    transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                    buildSigned(adminKeyPair)
                }
                Thread.sleep(200)
                iroha2Client.fireAndForget {
                    account(anotherDevAccountIdSender)
                    chainId(chainId)
                    transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                    buildSigned(adminKeyPair)
                }
                Thread.sleep(200)
                iroha2Client.fireAndForget {
                    account(anotherDevAccountIdSender)
                    chainId(chainId)
                    transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                    buildSigned(adminKeyPair)
                }
                Thread.sleep(200)
                iroha2Client.fireAndForget {
                    account(anotherDevAccountIdSender)
                    chainId(chainId)
                    transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                    buildSigned(adminKeyPair)
                }
                Thread.sleep(200)
                iroha2Client.fireAndForget {
                    account(anotherDevAccountIdSender)
                    chainId(chainId)
                    transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                    buildSigned(adminKeyPair)
                }
                Thread.sleep(200)
                iroha2Client.fireAndForget {
                    account(anotherDevAccountIdSender)
                    chainId(chainId)
                    transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                    buildSigned(adminKeyPair)
                }
                Thread.sleep(200)
                iroha2Client.fireAndForget {
                    account(anotherDevAccountIdSender)
                    chainId(chainId)
                    transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                    buildSigned(adminKeyPair)
                }
                iroha2Client.fireAndForget {
                    account(anotherDevAccountIdSender)
                    chainId(chainId)
                    transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                    buildSigned(adminKeyPair)
                }
                Thread.sleep(200)
                iroha2Client.fireAndForget {
                    account(anotherDevAccountIdSender)
                    chainId(chainId)
                    transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                    buildSigned(adminKeyPair)
                }
                Thread.sleep(200)
                iroha2Client.fireAndForget {
                    account(anotherDevAccountIdSender)
                    chainId(chainId)
                    transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                    buildSigned(adminKeyPair)
                }
                Thread.sleep(200)
                iroha2Client.fireAndForget {
                    account(anotherDevAccountIdSender)
                    chainId(chainId)
                    transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                    buildSigned(adminKeyPair)
                }
                Thread.sleep(200)
                iroha2Client.fireAndForget {
                    account(anotherDevAccountIdSender)
                    chainId(chainId)
                    transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                    buildSigned(adminKeyPair)
                }
                Thread.sleep(200)
                iroha2Client.fireAndForget {
                    account(anotherDevAccountIdSender)
                    chainId(chainId)
                    transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                    buildSigned(adminKeyPair)
                }
                Thread.sleep(200)
                iroha2Client.fireAndForget {
                    account(anotherDevAccountIdSender)
                    chainId(chainId)
                    transferAsset(anotherDevAssetIdSender, 1, targetDevAccountIdReceiver)
                    buildSigned(adminKeyPair)
                }
            }
            Session
        }
}