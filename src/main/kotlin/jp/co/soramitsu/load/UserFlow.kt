package jp.co.soramitsu.load

import io.gatling.javaapi.core.CoreDsl.csv
import io.gatling.javaapi.core.CoreDsl.scenario
import io.gatling.javaapi.core.ScenarioBuilder
import io.ktor.util.Identity.encode
import jp.co.soramitsu.iroha2.*
import jp.co.soramitsu.iroha2.client.Iroha2Client
import jp.co.soramitsu.iroha2.generated.*
import jp.co.soramitsu.iroha2.query.QueryBuilder
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig
import jp.co.soramitsu.load.toolbox.Wrench13
import kotlinx.coroutines.runBlocking
import java.security.KeyPair
import java.util.*

class UserFlow : Wrench13() {
    lateinit var domainIdSender: DomainId
    lateinit var anotherDevAccountIdSender: AccountId
    lateinit var anotherDevAssetIdSender: AssetId
    lateinit var targetDevAccountIdReceiver: AccountId
    lateinit var anotherDevKeyPairSender: KeyPair
    lateinit var signature: PublicKey
    lateinit var iroha2Client: Iroha2Client
    lateinit var txs: List<TransactionQueryOutput>
    lateinit var hash: ByteArray

    companion object {
        @JvmStatic
        fun apply() = runBlocking {
            UserFlow().applyScn()
        }
    }

    fun applyScn(): ScenarioBuilder {
        return userFlowScn
    }

    val userFlowScn = scenario("userFlowScn")
        .feed(csv("preconditionList.csv").circular())
        .exec { Session ->
            iroha2Client = buildClient(SimulationConfig.simulation.configuration())
            anotherDevKeyPairSender = adminKeyPair
            domainIdSender = Session.get<String>("domainIdSender")!!.asDomainId()
            anotherDevAccountIdSender = Session.get<String>("anotherDevAccountIdSender")!!.asAccountId()
            targetDevAccountIdReceiver = Session.get<String>("anotherDevAccountIdReceiver")!!.asAccountId()
            anotherDevAssetIdSender = Session.get<String>("anotherDevAssetIdSender")!!.asAssetId()
            Session
        }.exec { Session ->
            runBlocking {
                QueryBuilder.findAssetsByAccountId(anotherDevAccountIdSender)
                    .account(anotherDevAccountIdSender)
                    .buildSigned(anotherDevKeyPairSender)
                    .let { query ->
                        iroha2Client.sendQuery(query)
                    }
                println("findAssetsByAccountId")
            }
            Session
        }.exec { Session ->
            runBlocking {
                QueryBuilder.findAccountsByDomainId(domainIdSender)
                    .account(anotherDevAccountIdSender)
                    .buildSigned(anotherDevKeyPairSender)
                    .let { query ->
                        iroha2Client.sendQuery(query)
                    }
                println("findAccountsByDomainId")
            }
            Session
        }.exec { Session ->
            runBlocking {
                QueryBuilder.findAllAssets()
                    .account(anotherDevAccountIdSender)
                    .buildSigned(anotherDevKeyPairSender)
                    .let { query ->
                        iroha2Client.sendQuery(query)
                    }
                println("findAllAssets")
            }
            Session
        }.exec { Session ->
            runBlocking {
                QueryBuilder.findAllAssets()
                    .account(anotherDevAccountIdSender)
                    .buildSigned(anotherDevKeyPairSender)
                    .let { query ->
                        iroha2Client.sendQuery(query)
                    }
                println("findAllAssets")
            }
            Session
        }.exec { Session ->
            runBlocking {
                QueryBuilder.findAllTransactions()
                    .account(anotherDevAccountIdSender)
                    .buildSigned(anotherDevKeyPairSender)
                    .let { query ->
                        iroha2Client.sendQuery(query)
                    }
                println("findAllTransactions")
            }
            Session
        }.exec { Session ->
            runBlocking {
                QueryBuilder.findTransactionByHash(hash)
                    .account(anotherDevAccountIdSender)
                    .buildSigned(anotherDevKeyPairSender)
                    .let { query ->
                        iroha2Client.sendQuery(query)
                    }
                println("findTransactionByHash")
            }
            Session
        }.exec { Session ->
            runBlocking {
                QueryBuilder.findAccountsByDomainId(domainIdSender)
                    .account(anotherDevAccountIdSender)
                    .buildSigned(anotherDevKeyPairSender)
                    .let { query ->
                        iroha2Client.sendQuery(query)
                    }
                println("findAccountsByDomainId")
            }
            Session
        }.exec { Session ->
            runBlocking {
                QueryBuilder.findAllAssets()
                    .account(anotherDevAccountIdSender)
                    .buildSigned(anotherDevKeyPairSender)
                    .let { query ->
                        iroha2Client.sendQuery(query)
                    }
                println("findAllAssets")
            }
            Session
        }.exec { Session ->
            runBlocking {
                QueryBuilder.findAllAssets()
                    .account(anotherDevAccountIdSender)
                    .buildSigned(anotherDevKeyPairSender)
                    .let { query ->
                        iroha2Client.sendQuery(query)
                    }.also {

                    }
                println("findAllAssets")
            }
            Session
        }.exec { Session ->
            runBlocking {
                QueryBuilder.findAllTransactions()
                    .account(anotherDevAccountIdSender)
                    .buildSigned(anotherDevKeyPairSender)
                    .let { query ->
                        iroha2Client.sendQuery(query)
                    }
                println("findAllTransactions")
            }
            Session
        }
}