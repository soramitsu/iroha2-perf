package jp.co.soramitsu.load.base.scenarious

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.ScenarioBuilder
import kotlinx.coroutines.runBlocking

class SimpleScenario {
    companion object {
        @JvmStatic
        fun apply() = runBlocking {
            SimpleScenario().applyScn()
        }
    }

    fun applyScn(): ScenarioBuilder {
        return simpleScn
    }

    val simpleScn = CoreDsl.scenario("simpleScn")
        .exec { Session ->
            println("NOTHING_TO_DO, IT'S A SIMPLE SCN")
            Session
        }
}