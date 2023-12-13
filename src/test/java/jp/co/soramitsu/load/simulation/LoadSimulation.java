package jp.co.soramitsu.load.simulation;

import io.gatling.javaapi.core.OpenInjectionStep;
import io.gatling.javaapi.core.Simulation;
import jp.co.soramitsu.load.*;

public class LoadSimulation extends Simulation {
    {
        setUp(
                jp.co.soramitsu.load.SetUp.Companion.apply().injectOpen(OpenInjectionStep.atOnceUsers(1))
                        .andThen(Iroha2SetUp.Companion.apply().injectOpen(LoadProfiles.setupModel()))
                        .andThen(TransactionOnly.Companion.apply().injectClosed(LoadProfiles.getLoadClosedProfile()))
                        .andThen(CleanUp.Companion.apply().injectOpen(OpenInjectionStep.atOnceUsers(1)))
        );
    }
}
