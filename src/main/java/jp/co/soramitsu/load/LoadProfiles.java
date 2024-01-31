package jp.co.soramitsu.load;

import io.gatling.javaapi.core.ClosedInjectionStep;
import io.gatling.javaapi.core.OpenInjectionStep;
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig;

import static io.gatling.javaapi.core.CoreDsl.*;

public class LoadProfiles {
    public static ClosedInjectionStep getMaxPerformanceClosedProfile() {
        return incrementConcurrentUsers(SimulationConfig.simulation.concurrentUsers())
                .times(SimulationConfig.simulation.times())
                .eachLevelLasting(SimulationConfig.simulation.eachLevelLasting())
                .separatedByRampsLasting(SimulationConfig.simulation.separatedByRampsLasting())
                .startingFrom(SimulationConfig.simulation.startingFrom());
    }
    public static ClosedInjectionStep[] getStabilityClosedProfile() {
        return new ClosedInjectionStep[]{
                rampConcurrentUsers(0).to(SimulationConfig.simulation.intensity()).during(SimulationConfig.simulation.rampDuration()),
                constantConcurrentUsers(SimulationConfig.simulation.intensity()).during(SimulationConfig.simulation.stageDuration())
        };
    }
    public static ClosedInjectionStep[] getStressClosedProfile() {
        return new ClosedInjectionStep[]{
                rampConcurrentUsers(0).to(SimulationConfig.simulation.intensity()).during(SimulationConfig.simulation.rampDuration()),
                constantConcurrentUsers(SimulationConfig.simulation.intensity()).during(SimulationConfig.simulation.stageDuration()),
                rampConcurrentUsers(SimulationConfig.simulation.intensity()).to(SimulationConfig.simulation.stressIntensity()).during(SimulationConfig.simulation.stressRampDuration()),
                constantConcurrentUsers(SimulationConfig.simulation.stressIntensity()).during(SimulationConfig.simulation.stressDuration()),
                rampConcurrentUsers(SimulationConfig.simulation.stressIntensity()).to(SimulationConfig.simulation.intensity()).during(SimulationConfig.simulation.stressRampDuration()),
                constantConcurrentUsers(SimulationConfig.simulation.intensity()).during(SimulationConfig.simulation.stageDuration()),
                rampConcurrentUsers(SimulationConfig.simulation.intensity()).to(SimulationConfig.simulation.stressIntensity() * 2).during(SimulationConfig.simulation.stressRampDuration()),
                constantConcurrentUsers(SimulationConfig.simulation.stressIntensity() * 2).during(SimulationConfig.simulation.stressDuration()),
                rampConcurrentUsers(SimulationConfig.simulation.stressIntensity() * 2).to(SimulationConfig.simulation.intensity()).during(SimulationConfig.simulation.stressRampDuration()),
                constantConcurrentUsers(SimulationConfig.simulation.intensity()).during(SimulationConfig.simulation.stageDuration()),
                rampConcurrentUsers(SimulationConfig.simulation.intensity()).to(SimulationConfig.simulation.stressIntensity() * 3).during(SimulationConfig.simulation.stressRampDuration()),
                constantConcurrentUsers(SimulationConfig.simulation.stressIntensity() * 3).during(SimulationConfig.simulation.stressDuration()),
                rampConcurrentUsers(SimulationConfig.simulation.stressIntensity() * 3).to(SimulationConfig.simulation.intensity()).during(SimulationConfig.simulation.stressRampDuration()),
                constantConcurrentUsers(SimulationConfig.simulation.intensity()).during(SimulationConfig.simulation.stageDuration()),
        };
    }
    public static OpenInjectionStep[] setupModel(){
        return new OpenInjectionStep[]{
                atOnceUsers(SimulationConfig.simulation.domainSetUpRumpUp())
        };
    }
}
