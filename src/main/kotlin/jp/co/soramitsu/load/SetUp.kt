package jp.co.soramitsu.load

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.ScenarioBuilder
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import jp.co.soramitsu.load.objects.CustomHistogram
import jp.co.soramitsu.load.toolbox.Grinder
import jp.co.soramitsu.load.toolbox.Wrench13
import kotlinx.coroutines.runBlocking
@Controller("/setUp")
class SetUp {
    companion object {
        @JvmStatic
        fun apply() = runBlocking {
            SetUp().applyScn()
        }
    }
    @Get(produces = [MediaType.TEXT_PLAIN])
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