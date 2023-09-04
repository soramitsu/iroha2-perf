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

    public static OpenInjectionStep[] debugModel() {
        return new OpenInjectionStep[]{
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest())
        };
    }

    public static OpenInjectionStep[] maximumSearchModel(){
        return new OpenInjectionStep[]{
                constantUsersPerSec(SimulationConfig.simulation.maximumSearchRumpUp()).during(SimulationConfig.simulation.duringTest())
        };
    }

    public static OpenInjectionStep[] loadModel() {
        return new OpenInjectionStep[]{
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.stepDurationTest()),
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() * 2).during(SimulationConfig.simulation.stepDurationTest()),
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() * 3).during(SimulationConfig.simulation.stepDurationTest()),
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() * 4).during(SimulationConfig.simulation.stepDurationTest()),
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() * 5).during(SimulationConfig.simulation.stepDurationTest()),
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() * 6).during(SimulationConfig.simulation.stepDurationTest()),
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() * 7).during(SimulationConfig.simulation.stepDurationTest()),
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() * 8).during(SimulationConfig.simulation.stepDurationTest()),
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() * 9).during(SimulationConfig.simulation.stepDurationTest()),
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() * 10).during(SimulationConfig.simulation.stepDurationTest()),
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() * 11).during(SimulationConfig.simulation.stepDurationTest()),
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() * 12).during(SimulationConfig.simulation.stepDurationTest()),
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() * 13).during(SimulationConfig.simulation.stepDurationTest()),
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() * 14).during(SimulationConfig.simulation.stepDurationTest()),
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() * 15).during(SimulationConfig.simulation.stepDurationTest()),
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() * 16).during(SimulationConfig.simulation.stepDurationTest()),
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() * 17).during(SimulationConfig.simulation.stepDurationTest()),
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() * 18).during(SimulationConfig.simulation.stepDurationTest()),
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() * 19).during(SimulationConfig.simulation.stepDurationTest()),
                rampUsers(SimulationConfig.simulation.rampUpTest()).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() * 20).during(SimulationConfig.simulation.stepDurationTest())
        };
    }

    public static OpenInjectionStep[] stressModel(){
        return new OpenInjectionStep[]{
                rampUsers(SimulationConfig.simulation.rampUpTest() / 4).during(SimulationConfig.simulation.duringTest()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() / 4).during(SimulationConfig.simulation.stepDurationTest()),
                stressPeakUsers(SimulationConfig.simulation.rampUpTest() * 3).during(SimulationConfig.simulation.stressPeakDuration()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() / 4).during(SimulationConfig.simulation.stepDurationTest()),
                stressPeakUsers(SimulationConfig.simulation.rampUpTest() * 6).during(SimulationConfig.simulation.stressPeakDuration()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() / 4).during(SimulationConfig.simulation.stepDurationTest()),
                stressPeakUsers(SimulationConfig.simulation.rampUpTest() * 9).during(SimulationConfig.simulation.stressPeakDuration()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() / 4).during(SimulationConfig.simulation.stepDurationTest()),
                stressPeakUsers(SimulationConfig.simulation.rampUpTest() * 12).during(SimulationConfig.simulation.stressPeakDuration()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() / 4).during(SimulationConfig.simulation.stepDurationTest()),
                stressPeakUsers(SimulationConfig.simulation.rampUpTest() * 15).during(SimulationConfig.simulation.stressPeakDuration()),
                constantUsersPerSec(SimulationConfig.simulation.rampUpTest() / 4).during(SimulationConfig.simulation.stepDurationTest()),
        };
    }
}
