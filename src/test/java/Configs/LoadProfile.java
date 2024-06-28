package Configs;

import io.gatling.javaapi.core.OpenInjectionStep;

import static io.gatling.javaapi.core.CoreDsl.incrementUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;

public class LoadProfile {
    public static OpenInjectionStep getMaxPerformanceOpenProfile() {
        return incrementUsersPerSec(Double.parseDouble(System.getProperty("concurrentUsers")))
                .times(Integer.parseInt(System.getProperty("times")))
                .eachLevelLasting(Integer.parseInt(System.getProperty("stageDuration")))
                .separatedByRampsLasting(Integer.parseInt(System.getProperty("rampDuration")))
                .startingFrom(Integer.parseInt(System.getProperty("startingFrom")));
    }

    public static OpenInjectionStep getMaxPerformance() {
        return rampUsers(Integer.parseInt(System.getProperty("intensity")))
                .during(Long.parseLong(System.getProperty("rampDuration")));
    }
}
