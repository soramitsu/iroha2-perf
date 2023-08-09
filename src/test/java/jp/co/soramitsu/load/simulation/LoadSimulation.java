package jp.co.soramitsu.load.simulation;

import jp.co.soramitsu.load.base.LoadProfiles;
import jp.co.soramitsu.load.base.ScenarioSelector;
import io.gatling.javaapi.core.Simulation;
import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;
import static jp.co.soramitsu.load.base.Protocols.httpProtocol;

public class LoadSimulation extends Simulation {
    {
        setUp(
                ScenarioSelector.getScenario("Iroha2SetUp")
                        .injectOpen(atOnceUsers(1))
                                .andThen(
                                        ScenarioSelector.getScenario("TransferAssetsQueryStatus").injectOpen(LoadProfiles.loadModel())
                                )
        ).protocols(httpProtocol).maxDuration(120);
    }
}
