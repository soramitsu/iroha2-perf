package jp.co.soramitsu.load

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.ScenarioBuilder
import jp.co.soramitsu.load.objects.CustomHistogram
import jp.co.soramitsu.load.toolbox.Grinder
import kotlinx.coroutines.runBlocking
class SetUp {
    companion object {
        @JvmStatic
        fun apply() = runBlocking {
            SetUp().applyScn()
        }
    }
    fun applyScn(): ScenarioBuilder {
        return pushGatWaySetUpScn
    }

    val pushGatWaySetUpScn = CoreDsl.scenario("pushGatWaySetUpScn")
        .exec { Session ->
            Grinder()
            CustomHistogram()
            Session
        }
}