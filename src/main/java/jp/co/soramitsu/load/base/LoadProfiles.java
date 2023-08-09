package jp.co.soramitsu.load.base;

import io.gatling.javaapi.core.ClosedInjectionStep;
import io.gatling.javaapi.core.OpenInjectionStep;
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig;

import static io.gatling.javaapi.core.CoreDsl.*;

public class LoadProfiles {

    public static OpenInjectionStep[] loadModel(){
        return new OpenInjectionStep[]{
                rampUsers(SimulationConfig.simulation.rampUp()).during(SimulationConfig.simulation.during()),
                constantUsersPerSec(SimulationConfig.simulation.rampUp()).during(SimulationConfig.simulation.stepDuration()),
                rampUsers(SimulationConfig.simulation.rampUp()).during(SimulationConfig.simulation.during()),
                constantUsersPerSec(SimulationConfig.simulation.rampUp() * 2).during(SimulationConfig.simulation.stepDuration()),
                rampUsers(SimulationConfig.simulation.rampUp()).during(SimulationConfig.simulation.during()),
                constantUsersPerSec(SimulationConfig.simulation.rampUp() * 3).during(SimulationConfig.simulation.stepDuration()),
                rampUsers(SimulationConfig.simulation.rampUp()).during(SimulationConfig.simulation.during()),
                constantUsersPerSec(SimulationConfig.simulation.rampUp() * 4).during(SimulationConfig.simulation.stepDuration()),
                rampUsers(SimulationConfig.simulation.rampUp()).during(SimulationConfig.simulation.during()),
                constantUsersPerSec(SimulationConfig.simulation.rampUp() * 5).during(SimulationConfig.simulation.stepDuration())
        };
    }

    public static OpenInjectionStep[] maximumSearchModel(){
        return new OpenInjectionStep[]{
                constantUsersPerSec(SimulationConfig.simulation.maximumSearchRumpUp()).during(SimulationConfig.simulation.during())
        };
    }

    public static OpenInjectionStep[] stressModel(){
        return new OpenInjectionStep[]{
                rampUsers(SimulationConfig.simulation.rampUp()).during(SimulationConfig.simulation.during()),
                constantUsersPerSec(SimulationConfig.simulation.rampUp()).during(SimulationConfig.simulation.stepDuration()),
                stressPeakUsers(SimulationConfig.simulation.stressPeakRampUp()).during(SimulationConfig.simulation.stressPeakDuration()),
                constantUsersPerSec(SimulationConfig.simulation.rampUp()).during(SimulationConfig.simulation.stepDuration()),
                stressPeakUsers(SimulationConfig.simulation.stressPeakRampUp()).during(SimulationConfig.simulation.stressPeakDuration()),
        };
    }
}
