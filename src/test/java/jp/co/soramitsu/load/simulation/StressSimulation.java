package jp.co.soramitsu.load.simulation;

import io.gatling.javaapi.core.Simulation;
import jp.co.soramitsu.load.base.LoadProfiles;
import jp.co.soramitsu.load.base.ScenarioSelector;

import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;
import static jp.co.soramitsu.load.base.Protocols.httpProtocol;

public class StressSimulation extends Simulation {
    {
        setUp(
                ScenarioSelector.getScenario("Iroha2SetUp")
                        .injectOpen(atOnceUsers(1))
                        .andThen(
                                ScenarioSelector.getScenario("TransferAssetsQueryStatus").injectOpen(LoadProfiles.stressModel())
                        )
        ).protocols(httpProtocol).maxDuration(120);
    }
}
