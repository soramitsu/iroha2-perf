package jp.co.soramitsu.load;

import io.gatling.javaapi.core.OpenInjectionStep;
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig;

import static io.gatling.javaapi.core.CoreDsl.*;

public class LoadProfiles {
    public static OpenInjectionStep[] setupModel(){
        return new OpenInjectionStep[]{
                rampUsers(SimulationConfig.simulation.domainSetUpRumpUp()).during(SimulationConfig.simulation.duringSetUp())
        };
    }
    public static OpenInjectionStep[] loadModel() {
        return new OpenInjectionStep[]{
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest())
                /*constantUsersPerSec(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.stepDurationTest()),
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() * 2).during(SimulationConfig.simulation.stepDurationTest()),
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() * 3).during(SimulationConfig.simulation.stepDurationTest()),
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() * 4).during(SimulationConfig.simulation.stepDurationTest()),
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() * 5).during(SimulationConfig.simulation.stepDurationTest())*/
        };
    }
    public static OpenInjectionStep[] maximumSearchModel(){
        return new OpenInjectionStep[]{
                constantUsersPerSec(SimulationConfig.simulation.maximumSearchRumpUp()).during(SimulationConfig.simulation.duringTest())
        };
    }
    public static OpenInjectionStep[] stressModel(){
        return new OpenInjectionStep[]{
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.stepDurationTest()),
                stressPeakUsers(SimulationConfig.simulation.stressPeakRampUp()).during(SimulationConfig.simulation.stressPeakDuration()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.stepDurationTest()),
                stressPeakUsers(SimulationConfig.simulation.stressPeakRampUp()).during(SimulationConfig.simulation.stressPeakDuration()),
        };
    }
}
