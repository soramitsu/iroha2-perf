package jp.co.soramitsu.load.simulation;

import io.gatling.javaapi.core.OpenInjectionStep;
import io.gatling.javaapi.core.Simulation;
import jp.co.soramitsu.load.CleanUp;
import jp.co.soramitsu.load.LoadProfiles;
import jp.co.soramitsu.load.triggers.*;

public class LoadSimulationTrigger extends Simulation {
    {
        setUp(
                jp.co.soramitsu.load.SetUp.Companion.apply().injectOpen(OpenInjectionStep.atOnceUsers(1))
                        .andThen(SetUpWasmTrigger.Companion.apply().injectOpen(OpenInjectionStep.atOnceUsers(1)))
                        .andThen(WasmTrigger.Companion.apply().injectClosed(LoadProfiles.getLoadClosedProfile()))
                        .andThen(CleanUp.Companion.apply().injectOpen(OpenInjectionStep.atOnceUsers(1)))
        );
    }

}
