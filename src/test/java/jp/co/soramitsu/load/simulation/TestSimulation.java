package jp.co.soramitsu.load.simulation;

import io.gatling.javaapi.core.Simulation;
import jp.co.soramitsu.load.LoadProfiles;
import jp.co.soramitsu.load.Protocols;
import jp.co.soramitsu.load.ScenarioSelector;

import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;

public class TestSimulation extends Simulation {
    {
        setUp(
                ScenarioSelector.getScenario()
                        .injectOpen(atOnceUsers(1))
                        .andThen(
                                ScenarioSelector.getScenario().injectOpen(LoadProfiles.loadModel())
                        )
        ).protocols(Protocols.httpProtocol).maxDuration(120);
    }
}
