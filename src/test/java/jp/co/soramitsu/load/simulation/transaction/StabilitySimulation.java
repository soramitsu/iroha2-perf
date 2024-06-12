package jp.co.soramitsu.load.simulation.transaction;

import io.gatling.javaapi.core.OpenInjectionStep;
import io.gatling.javaapi.core.Simulation;
import jp.co.soramitsu.load.*;

public class StabilitySimulation extends Simulation {
    {
        setUp(
                //jp.co.soramitsu.load.SetUp.Companion.apply().injectOpen(OpenInjectionStep.atOnceUsers(1))
                        TransferAssets.Companion.apply().injectClosed(LoadProfiles.getStabilityClosedProfile())
                        //.andThen(CleanUp.Companion.apply().injectOpen(OpenInjectionStep.atOnceUsers(1)))
        );
    }
}
