package jp.co.soramitsu.load.simulation.smartcontract;

import io.gatling.javaapi.core.OpenInjectionStep;
import io.gatling.javaapi.core.Simulation;
import jp.co.soramitsu.load.CleanUp;
import jp.co.soramitsu.load.LoadProfiles;
import jp.co.soramitsu.load.Protocols;
import jp.co.soramitsu.load.TechicalScns.Iroha2SetUp;
import jp.co.soramitsu.load.triggers.SetUpWasmTrigger;
import jp.co.soramitsu.load.triggers.WasmTrigger;


public class StressSimulation extends Simulation {
    {
        setUp(
                jp.co.soramitsu.load.SetUp.Companion.apply().injectOpen(OpenInjectionStep.atOnceUsers(1))
                        .andThen(SetUpWasmTrigger.Companion.apply().injectOpen(OpenInjectionStep.atOnceUsers(1)))
                        .andThen(WasmTrigger.Companion.apply().injectClosed(LoadProfiles.getStressClosedProfile()))
                        .andThen(CleanUp.Companion.apply().injectOpen(OpenInjectionStep.atOnceUsers(1)))
        ).protocols(Protocols.httpProtocol);
    }
}
