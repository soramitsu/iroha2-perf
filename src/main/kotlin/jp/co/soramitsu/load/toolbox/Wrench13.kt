package jp.co.soramitsu.load.toolbox

import io.prometheus.client.Histogram
import io.prometheus.client.Histogram.Timer
import io.prometheus.client.exporter.PushGateway
import jp.co.soramitsu.iroha2.asDomainId
import jp.co.soramitsu.iroha2.asName
import jp.co.soramitsu.iroha2.client.Iroha2Client
import jp.co.soramitsu.iroha2.generated.AccountId
import jp.co.soramitsu.iroha2.generated.AssetId
import jp.co.soramitsu.iroha2.generated.AssetValue
import jp.co.soramitsu.iroha2.generated.DomainId
import jp.co.soramitsu.iroha2.keyPairFromHex
import jp.co.soramitsu.load.objects.AnotherDevs
import jp.co.soramitsu.load.objects.Transaction
import java.net.URL
import java.security.KeyPair

open class Wrench13 {
    val admin = AccountId("bob".asName(), "wonderland".asDomainId())
    val adminKeyPair = keyPairFromHex(
        "7233bfc89dcbd68c19fde6ce6158225298ec1131b6a130d1aeb454c1ab5183c0",
        "9ac47abf59b356e0bd7dcbbbb4dec080e302156a48ca907e47cb6aea1d32719e",
    )

    //local connect
    val Iroha2Client: Iroha2Client = Iroha2Client(URL("http://127.0.0.1:8080"), true, null)

    //remote connect
    //val Iroha2Client: Iroha2Client = Iroha2Client(URL("https://iroha2.dev.tachi.soramitsu.co.jp:8080"), true, "iroha2-dev:psfFzyjGRzCbE0ELUzRw4GZyFQprM4D5", 10000)

    var pliers: Pliers = Pliers()
    //val transaction: Transaction = Transaction()
    var pushGateway = PushGateway("127.0.0.1:9091");

    lateinit var listener: BlueElectricalTape
    lateinit var anotherDevDomainId: DomainId
    lateinit var currentDevAccountId: AccountId
    lateinit var currentDevKeyPair: KeyPair
    lateinit var currentDevAssetId: AssetId
    lateinit var targetDevAccountId: AccountId
    lateinit var targetDevKeyPair: KeyPair
    lateinit var currentDevAssetIdBeforTransferring: AssetValue
    lateinit var currentDevAssetIdAfterTransferring: AssetValue
    lateinit var targetDevAssetIdAfterTransferring: AssetValue
    lateinit var timer: Timer

    var queryWaiter: Long = 1500
    var transactionWaiter: Long = 10
    var userRequestCounter: Int = 10
    var attemptsPersentage: Int = 2
    var attempt: Int = -1

    var anotherDevDomainIdList: MutableList<DomainId> = mutableListOf()

    fun sendMetricsToPrometheus(histogram: Histogram, job: String) {
        try {
            pushGateway.pushAdd(histogram, job)
        } catch (ex: Exception) {
            ex.message
        }
    }
}