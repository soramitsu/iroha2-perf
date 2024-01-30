package jp.co.soramitsu.load.simulation.transaction;

import io.gatling.javaapi.core.OpenInjectionStep;
import io.gatling.javaapi.core.Simulation;
import jp.co.soramitsu.load.*;

public class PerformanceSimulation extends Simulation {
    {
        setUp(
                jp.co.soramitsu.load.SetUp.Companion.apply().injectOpen(OpenInjectionStep.atOnceUsers(1))
                        .andThen(TransferAssets.Companion.apply().injectClosed(LoadProfiles.getMaxPerformanceClosedProfile()))
                        .andThen(CleanUp.Companion.apply().injectOpen(OpenInjectionStep.atOnceUsers(1)))
        ).protocols(Protocols.httpProtocol);
    }
}
