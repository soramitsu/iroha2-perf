package jp.co.soramitsu.load.simulation;

import io.gatling.javaapi.core.OpenInjectionStep;
import io.gatling.javaapi.core.Simulation;
import jp.co.soramitsu.load.*;

import java.time.Duration;

import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;
import static io.gatling.javaapi.core.OpenInjectionStep.nothingFor;

public class MaximumSearchSimulation extends Simulation {
    {
        setUp(
                jp.co.soramitsu.load.SetUp.Companion.apply().injectOpen(OpenInjectionStep.atOnceUsers(1))
                        .andThen(Iroha2SetUp.Companion.apply().injectOpen(LoadProfiles.setupModel()))
                        .andThen(TransactionOnly.Companion.apply().injectClosed(LoadProfiles.getMaxPerformanceClosedProfile()))
                        .andThen(CleanUp.Companion.apply().injectOpen(OpenInjectionStep.atOnceUsers(1)))
        ).protocols(Protocols.httpProtocol);
    }
}
