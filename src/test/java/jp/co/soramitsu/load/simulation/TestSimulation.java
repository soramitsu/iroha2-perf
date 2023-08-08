package jp.co.soramitsu.load.simulation;

import jp.co.soramitsu.load.base.ScenarioSelector;
import io.gatling.javaapi.core.Simulation;
import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;
import static jp.co.soramitsu.load.base.Protocols.httpProtocol;

public class TestSimulation extends Simulation {
    {
        setUp(
                ScenarioSelector.getScenario("Iroha2SetUp").injectOpen(atOnceUsers(1))
                                .andThen(
                                        ScenarioSelector.getScenario("TransferAssetsQueryStatus").injectOpen(atOnceUsers(1))
                                )
        ).protocols(httpProtocol).maxDuration(120);
    }
}
