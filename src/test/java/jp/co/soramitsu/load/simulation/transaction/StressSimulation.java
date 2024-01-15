package jp.co.soramitsu.load.simulation.transaction;

import io.gatling.javaapi.core.OpenInjectionStep;
import io.gatling.javaapi.core.Simulation;
import jp.co.soramitsu.load.*;
import jp.co.soramitsu.load.TechicalScns.Iroha2SetUp;


public class StressSimulation extends Simulation {
    {
        setUp(
                jp.co.soramitsu.load.SetUp.Companion.apply().injectOpen(OpenInjectionStep.atOnceUsers(1))
                        .andThen(TransferAssets.Companion.apply().injectClosed(LoadProfiles.getLoadClosedProfile()))
                        .andThen(CleanUp.Companion.apply().injectOpen(OpenInjectionStep.atOnceUsers(1)))
        ).protocols(Protocols.httpProtocol);
    }
}
