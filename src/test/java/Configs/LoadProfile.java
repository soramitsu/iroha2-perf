package Configs;

import io.gatling.javaapi.core.OpenInjectionStep;

import static io.gatling.javaapi.core.CoreDsl.incrementUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;

public class LoadProfile {
    public static OpenInjectionStep getMaxPerformanceOpenProfile() {
        return incrementUsersPerSec(Double.parseDouble(System.getProperty("concurrentUsers")))//20
                .times(Integer.parseInt(System.getProperty("times")))//5
                .eachLevelLasting(Integer.parseInt(System.getProperty("stageDuration")))//5
                .separatedByRampsLasting(Integer.parseInt(System.getProperty("rampDuration")))//5
                .startingFrom(Integer.parseInt(System.getProperty("startingFrom")));//20
    }

    public static OpenInjectionStep getMaxPerformance() {
        return rampUsers(Integer.parseInt(System.getProperty("intensity")))
                .during(Long.parseLong(System.getProperty("rampDuration")));
    }
}
