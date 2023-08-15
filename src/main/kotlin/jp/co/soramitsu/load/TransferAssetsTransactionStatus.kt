package jp.co.soramitsu.load

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.CoreDsl.exec
import io.gatling.javaapi.core.ScenarioBuilder
import jp.co.soramitsu.iroha2.query.QueryBuilder
import jp.co.soramitsu.load.objects.AnotherDevs
import jp.co.soramitsu.load.toolbox.BlueElectricalTape
import jp.co.soramitsu.load.toolbox.Wrench13
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.withTimeout
import java.time.Duration
import kotlin.random.Random

class TransferAssetsTransactionStatus: Wrench13() {
    companion object {
        @JvmStatic
        fun apply() = runBlocking {
            TransferAssetsTransactionStatus().applyScn()
        }
    }

    fun applyScn(): ScenarioBuilder {
        return transferAssetsScn
    }

    val transferAssetsScn = CoreDsl.scenario("TransferAssets")
        .exec { Session ->
            listener = BlueElectricalTape()
            Session
        }
        .repeat(userRequestCounter)
        .on(
            exec { Session ->
                var currentDevIndex: Int = Random.nextInt(0, AnotherDevs.list.size - 1)
                currentDevAccountId = AnotherDevs.list[currentDevIndex].anotherDevAccountId
                currentDevKeyPair = AnotherDevs.list[currentDevIndex].anotherDevKeyPair
                do {
                    var targetDevIndex: Int = Random.nextInt(0, AnotherDevs.list.size - 1)
                    targetDevAccountId = AnotherDevs.list[targetDevIndex].anotherDevAccountId
                    targetDevKeyPair = AnotherDevs.list[targetDevIndex].anotherDevKeyPair
                } while (targetDevAccountId.name.string == currentDevAccountId.name.string)
                Session
            }
            .exec { Session ->
            listener.metricRegistry.timer("query_find_asset_by_account_id").time(
                Runnable {
                    runBlocking {
                        QueryBuilder.findAssetsByAccountId(accountId = currentDevAccountId)
                            .account(currentDevAccountId)
                            .buildSigned(currentDevKeyPair)
                            .let { query ->
                                Iroha2Client.sendQuery(query)
                            }.let {
                                currentDevAssetId = it.get(0).id
                            }
                    }
                }
            )
            Thread.sleep(5000)
            attempt++
            if(attempt % attemptsPersentage == 0){
                val newSession = Session.set("condition", true)
                newSession
            } else {
                val newSession = Session.set("condition", false)
                newSession
            }
            }.doIf{ Session -> Session.getBoolean("condition") }
                .then(
                    exec { Session ->
                        listener.metricRegistry.timer("subscription_to_block_stream").time(
                            Runnable {
                                pliers.getSubscribetionToBlockStream(Iroha2Client, 1, 2)
                            }
                        )
                        listener.metricRegistry.timer("transaction_transfer_asset").time(
                            Runnable {
                                runBlocking {
                                    Iroha2Client.sendTransaction {
                                        account(currentDevAccountId)
                                        transferAsset(currentDevAssetId, 10, targetDevAccountId)
                                        buildSigned(currentDevKeyPair)
                                    }.also { d ->
                                        withTimeout(Duration.ofSeconds(transactionWaiter)) { d.await() }
                                    }
                                }
                            }
                        )
                        /*runBlocking {
                            pliers.getLastDomain()
                        }*/
                        val newSession = Session.set("condition", false)
                        newSession
                    }
                )
        )
        .exec{ Session ->
            listener.stop()
            listener.stopReporter()
            Session
        }
}