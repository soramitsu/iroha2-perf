package jp.co.soramitsu.load

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.CoreDsl.exec
import io.gatling.javaapi.core.ScenarioBuilder
import jp.co.soramitsu.iroha2.query.QueryBuilder
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig
import jp.co.soramitsu.load.objects.AnotherDevs
import jp.co.soramitsu.load.objects.CustomHistogram
import jp.co.soramitsu.load.toolbox.Grinder
import jp.co.soramitsu.load.toolbox.Wrench13
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.withTimeout
import org.testng.Assert
import java.time.Duration
import kotlin.random.Random

class TransferAssetsQueryStatus: Wrench13() {
    companion object {
        @JvmStatic
        fun apply() = runBlocking {
            TransferAssetsQueryStatus().applyScn()
        }
    }

    fun applyScn(): ScenarioBuilder {
        return transferAssetsQueryStatusScn
    }

    val transferAssetsQueryStatusScn = CoreDsl.scenario("TransferAssets")
        .repeat(SimulationConfig.simulation.attemptsToTransaction)
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
                timer = CustomHistogram.findAssetsByAccountIdQueryTimer.labels(
                    "gatling"
                    , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                    , Iroha2SetUp::class.simpleName
                ).startTimer()
                try {
                    runBlocking {
                        QueryBuilder.findAssetsByAccountId(accountId = currentDevAccountId)
                            .account(currentDevAccountId)
                            .buildSigned(currentDevKeyPair)
                            .let { query ->
                                Iroha2Client.sendQuery(query)
                            }.let {
                                currentDevAssetId = it.get(0).id
                                currentDevAssetIdBeforTransferring = it.get(0).value
                            }
                    }
                } finally {
                    timer.observeDuration()
                    sendMetricsToPrometheus(CustomHistogram.findAssetsByAccountIdQueryTimer, "query")
                }
                Thread.sleep(queryWaiter)
                Session
            }
            .exec { Session ->
                timer = CustomHistogram.findAssetsByAccountIdQueryTimer.labels(
                    "gatling"
                    , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                    , Iroha2SetUp::class.simpleName
                ).startTimer()
                try {
                    runBlocking {
                        QueryBuilder.findAssetsByAccountId(accountId = targetDevAccountId)
                            .account(targetDevAccountId)
                            .buildSigned(targetDevKeyPair)
                            .let { query ->
                                Iroha2Client.sendQuery(query)
                            }.let {
                                targetDevAssetIdAfterTransferring = it.get(0).value
                            }
                    }
                } finally {
                    timer.observeDuration()
                    sendMetricsToPrometheus(CustomHistogram.findAssetsByAccountIdQueryTimer, "query")
                }
                Thread.sleep(queryWaiter)
                attempt++
                if(attempt % SimulationConfig.simulation.attemptsToTransferTransaction == 0){
                    val newSession = Session.set("condition", true)
                    newSession
                } else {
                    val newSession = Session.set("condition", false)
                    newSession
                }
            }.doIf{ Session -> Session.getBoolean("condition") }
                .then(
                    exec { Session ->
                        timer = CustomHistogram.subscriptionToBlockStream.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName
                        ).startTimer()
                        try {
                            pliers.getSubscribetionToBlockStream(Iroha2Client, 1, 2)
                        } finally {
                            timer.observeDuration()
                            sendMetricsToPrometheus(CustomHistogram.subscriptionToBlockStream, "subscription")
                        }
                        timer = CustomHistogram.transferAssetTransactionTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName
                        ).startTimer()
                        try {
                            runBlocking {
                                Iroha2Client.sendTransaction {
                                    account(currentDevAccountId)
                                    transferAsset(currentDevAssetId, 1, targetDevAccountId)
                                    buildSigned(currentDevKeyPair)
                                }.also { d ->
                                    withTimeout(Duration.ofSeconds(transactionWaiter)) { d.await() }
                                }
                            }
                        } finally {
                            timer.observeDuration()
                            sendMetricsToPrometheus(CustomHistogram.transferAssetTransactionTimer, "transaction")
                        }
                        /*runBlocking {
                            pliers.getLastDomain()
                        }*/
                        val newSession = Session.set("condition", false)
                        newSession
                    }
                    .exec { Session ->
                        timer = CustomHistogram.findAssetsByAccountIdQueryTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName
                        ).startTimer()
                        try {
                            runBlocking {
                                QueryBuilder.findAssetsByAccountId(accountId = currentDevAccountId)
                                    .account(currentDevAccountId)
                                    .buildSigned(currentDevKeyPair)
                                    .let { query ->
                                        Iroha2Client.sendQuery(query)
                                    }.let {
                                        currentDevAssetIdAfterTransferring = it.get(0).value
                                    }
                            }
                        } finally {
                            timer.observeDuration()
                            sendMetricsToPrometheus(CustomHistogram.findAssetsByAccountIdQueryTimer, "query")
                        }

                        Thread.sleep(queryWaiter)
                        Assert.assertNotEquals(currentDevAssetIdBeforTransferring, currentDevAssetIdAfterTransferring
                            , "transaction \'transfer_asset\' from ${currentDevAccountId} to ${targetDevAccountId} failed")
                        Session
                    }
                    .exec { Session ->
                        timer = CustomHistogram.findAssetsByAccountIdQueryTimer.labels(
                            "gatling"
                            , System.getProperty("user.dir").substringAfterLast("/").substringAfterLast("\\")
                            , Iroha2SetUp::class.simpleName
                        ).startTimer()
                        try {
                            runBlocking {
                                QueryBuilder.findAssetsByAccountId(accountId = targetDevAccountId)
                                    .account(targetDevAccountId)
                                    .buildSigned(targetDevKeyPair)
                                    .let { query ->
                                        Iroha2Client.sendQuery(query)
                                    }.let {
                                        targetDevAssetIdAfterTransferring = it.get(0).value
                                    }
                            }
                        } finally {
                            timer.observeDuration()
                            sendMetricsToPrometheus(CustomHistogram.findAssetsByAccountIdQueryTimer, "query")
                        }
                        Thread.sleep(queryWaiter)
                        Assert.assertEquals(targetDevAssetIdAfterTransferring, targetDevAssetIdAfterTransferring
                            , "transaction \'transfer_asset\' from ${currentDevAccountId} to ${targetDevAccountId} failed")
                        Session
                    }
                )
        )
}
