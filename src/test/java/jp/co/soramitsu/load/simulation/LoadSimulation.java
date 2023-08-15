package jp.co.soramitsu.load.simulation;

import io.gatling.javaapi.core.Simulation;
import jp.co.soramitsu.load.*;
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig;

import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;


public class LoadSimulation extends Simulation {
    {
        setUp(
                Iroha2SetUp.Companion.apply()
                        .injectOpen(atOnceUsers(SimulationConfig.simulation.rampUp()))
                                .andThen(
                                        TransferAssetsQueryStatus.Companion.apply().injectOpen(LoadProfiles.loadModel())
                                )
        ).protocols(Protocols.httpProtocol).maxDuration(80);
    }
}
