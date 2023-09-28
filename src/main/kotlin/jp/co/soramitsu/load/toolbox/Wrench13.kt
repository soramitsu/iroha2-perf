package jp.co.soramitsu.load.toolbox

import io.prometheus.client.Histogram
import io.prometheus.client.Histogram.Timer
import io.prometheus.client.Counter
import io.prometheus.client.exporter.PushGateway
import jp.co.soramitsu.iroha2.asDomainId
import jp.co.soramitsu.iroha2.asName
import jp.co.soramitsu.iroha2.client.Iroha2Client
import jp.co.soramitsu.iroha2.generated.AccountId
import jp.co.soramitsu.iroha2.generated.AssetId
import jp.co.soramitsu.iroha2.generated.AssetValue
import jp.co.soramitsu.iroha2.generated.DomainId
import jp.co.soramitsu.iroha2.keyPairFromHex

import org.apache.http.client.utils.URIBuilder
import java.net.URL
import java.security.KeyPair

open class Wrench13 {
    //curl -X GET https://iroha2.test.tachi.soramitsu.co.jp/peer-0/telemetry/metrics -u 'iroha2-dev:4H7L&KN25%$2jisV8&NTVtiX'
    // http://iroha2-peer-0.iroha2-test.svc.cluster.local:8080
    val urls: MutableList<URL> = mutableListOf()

    val admin = AccountId("bob".asName(), "wonderland".asDomainId())
    val adminKeyPair = keyPairFromHex(
        "7233bfc89dcbd68c19fde6ce6158225298ec1131b6a130d1aeb454c1ab5183c0",
        "9ac47abf59b356e0bd7dcbbbb4dec080e302156a48ca907e47cb6aea1d32719e",
    )

    val Iroha2Client: Iroha2Client = buildIroha2Client()

    fun buildIroha2Client(): Iroha2Client{
        val peerUrl = URIBuilder().let {
            it.scheme = "https"
            it.host = "iroha2.test.tachi.soramitsu.co.jp"
            it.port = 0
            it.path = "peer-0/api"
            it.build().toURL()
        }
        urls.add(peerUrl)
        return Iroha2Client(
            urls[0],
            urls[0],
            urls[0],
            log = false,
            credentials = "iroha2-dev:4H7L&KN25%$2jisV8&NTVtiX",
            eventReadTimeoutInMills = 10000,
            eventReadMaxAttempts = 20
        )
    }

    var pushGateway = PushGateway("127.0.0.1:9091");

    lateinit var anotherDevDomainId: DomainId
    lateinit var currentDevAccountId: AccountId
    lateinit var currentDevKeyPair: KeyPair
    lateinit var currentDevAssetId: AssetId
    lateinit var targetDevAccountId: AccountId
    lateinit var targetDevKeyPair: KeyPair
    lateinit var currentDevAssetIdBeforeTransferring: AssetValue
    lateinit var currentDevAssetIdAfterTransferring: AssetValue
    lateinit var targetDevAssetIdAfterTransferring: AssetValue
    lateinit var timer: Timer
    lateinit var counter: Counter.Child

    var queryWaiter: Long = 5000 //ms
    var transactionWaiter: Long = 30 //s
    var userRequestCounter: Int = 10
    var attemptsPersentage: Int = 2
    var attempt: Int = -1

    var anotherDevDomainIdList: MutableList<DomainId> = mutableListOf()

/*    fun sendMetricsToPrometheus(histogram: Histogram, job: String) {
        try {
            pushGateway.pushAdd(histogram, job)
        } catch (ex: Exception) {
            ex.message
        }
    }*/
    fun sendMetricsToPrometheus(counter: Counter, job: String) {
        try {
            pushGateway.pushAdd(counter, job)
        } catch (ex: Exception) {
            ex.message
        }
    }
}