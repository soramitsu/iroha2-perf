package jp.co.soramitsu.load.toolbox

import io.prometheus.client.Histogram
import io.prometheus.client.Histogram.Timer
import io.prometheus.client.Counter
import io.prometheus.client.exporter.PushGateway
import io.prometheus.client.exporter.HTTPServer
import jp.co.soramitsu.iroha2.asDomainId
import jp.co.soramitsu.iroha2.asName
import jp.co.soramitsu.iroha2.client.Iroha2Client
import jp.co.soramitsu.iroha2.client.blockstream.BlockStreamSubscription
import jp.co.soramitsu.iroha2.generated.AccountId
import jp.co.soramitsu.iroha2.generated.AssetId
import jp.co.soramitsu.iroha2.generated.AssetValue
import jp.co.soramitsu.iroha2.generated.DomainId
import jp.co.soramitsu.iroha2.keyPairFromHex
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig
import org.apache.http.client.utils.URIBuilder
import java.net.URL
import java.security.KeyPair

open class Wrench13 {

    val urls: MutableList<URL> = mutableListOf()

    val admin = AccountId("wonderland".asDomainId(), "bob".asName())
    val adminKeyPair = keyPairFromHex(
        "7233bfc89dcbd68c19fde6ce6158225298ec1131b6a130d1aeb454c1ab5183c0",
        "9ac47abf59b356e0bd7dcbbbb4dec080e302156a48ca907e47cb6aea1d32719e",
    )
    var queryWaiter: Long = 5000 //ms
    var transactionWaiter: Long = 60 //s
    var userRequestCounter: Int = 10
    var attemptsPersentage: Int = 2
    var attempt: Int = -1
    var anotherDevDomainIdList: MutableList<DomainId> = mutableListOf()
    //var Iroha2Client: Iroha2Client = buildClient("peer-0/api")
    var pushGateway = PushGateway("pushgateway:9091");

    lateinit var Iroha2Client: Iroha2Client
    lateinit var currentDevAccountId: AccountId
    lateinit var currentDevKeyPair: KeyPair
    lateinit var currentDevAssetId: AssetId
    lateinit var targetDevAccountId: AccountId
    lateinit var targetDevKeyPair: KeyPair
    lateinit var currentDevAssetIdBeforeTransferring: AssetValue
    lateinit var currentDevAssetIdAfterTransferring: AssetValue
    lateinit var targetDevAssetIdAfterTransferring: AssetValue
    lateinit var subscription: BlockStreamSubscription
    lateinit var timer: Timer

    val peers = arrayOf("peer-0/api", "peer-1/api", "peer-2/api", "peer-3/api", "peer-4/api")

    fun buildClient(peer : String): Iroha2Client {
        val peerUrl = URIBuilder().let {
            it.scheme = SimulationConfig.simulation.targetProtocol()
            it.host = SimulationConfig.simulation.targetURL()
            it.port = 0
            it.path = peer
            it.build().toURL()
        }
        urls.add(peerUrl)
        return Iroha2Client(
            urls[0],
            urls[0],
            urls[0],
            log = false,
            credentials = SimulationConfig.simulation.remoteLogin() + ":" + SimulationConfig.simulation.remotePass(),
            eventReadTimeoutInMills = 10000,
            eventReadMaxAttempts = 20
        )
    }

    fun sendMetricsToPrometheus(histogram: Histogram, job: String) {
        try {
            pushGateway.pushAdd(histogram, job)
        } catch (ex: Exception) {
            ex.message
        }
    }

    fun sendMetricsToPrometheus(counter: Counter, job: String) {
        try {
            pushGateway.pushAdd(counter, job)
        } catch (ex: Exception) {
            ex.message
        }
    }
}