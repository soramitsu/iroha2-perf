package jp.co.soramitsu.load.simulation;

import io.gatling.javaapi.core.OpenInjectionStep;
import io.gatling.javaapi.core.Simulation;
import jp.co.soramitsu.load.*;

import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;

public class MaximumSearchSimulation extends Simulation {
    /*{
        setUp(
                jp.co.soramitsu.load.SetUp.Companion.apply().injectOpen(OpenInjectionStep.atOnceUsers(1))
                        .andThen(Iroha2SetUp.Companion.apply().injectOpen(LoadProfiles.setupModel())
                                .andThen(TransactionOnly.Companion.apply().injectClosed(LoadProfiles.getMaxPerformanceClosedProfile()))
                        ).andThen(CleanUp.Companion.apply().injectOpen(OpenInjectionStep.atOnceUsers(1)))
        ).protocols(Protocols.httpProtocol);
    }*/
    {
        setUp(
                jp.co.soramitsu.load.SetUp.Companion.apply().injectClosed(LoadProfiles.closedSetUp())
                        .andThen(Iorha2DomainSetUp.Companion.apply().injectClosed(LoadProfiles.closedDomainSetUpModel()))
                        .andThen(Iroha2UserSetUp.Companion.apply().injectClosed(LoadProfiles.closedUserSetUpModel()))
                        .andThen(TransactionOnly.Companion.apply().injectClosed(LoadProfiles.getMaxPerformanceClosedProfile()))
                        .andThen(CleanUp.Companion.apply().injectClosed(LoadProfiles.closedCleanUp()))
        ).protocols(Protocols.httpProtocol);
    }
}
