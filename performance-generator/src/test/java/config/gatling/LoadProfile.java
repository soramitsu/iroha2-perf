package config.gatling;

import io.gatling.javaapi.core.OpenInjectionStep;

import static io.gatling.javaapi.core.CoreDsl.*;

public class LoadProfile {
    public static OpenInjectionStep getMaxPerformanceGradualLoadOpenProfile() {
        return incrementUsersPerSec(Double.parseDouble(System.getProperty("concurrentUsers")))//20
                .times(Integer.parseInt(System.getProperty("times")))//5
                .eachLevelLasting(Integer.parseInt(System.getProperty("stageDuration")))//5
                .startingFrom(Integer.parseInt(System.getProperty("startingFrom")));//20
    }

    public static OpenInjectionStep[] getMaxPerformance() {
        return new OpenInjectionStep[]{
                rampUsers(Integer.parseInt(System.getProperty("intensity"))).during(Long.parseLong(System.getProperty("rampDuration")))
                , constantUsersPerSec(Integer.parseInt(System.getProperty("intensity"))).during(Long.parseLong(System.getProperty("maxDuration")) - Long.parseLong(System.getProperty("rampDuration")))
        };
    }

    public static OpenInjectionStep[] getRampConstantUserPerSecond() {
        return new OpenInjectionStep[]{
                rampUsersPerSec(Integer.parseInt(System.getProperty("startLevelUsers")))
                        .to(Integer.parseInt(System.getProperty("endLevelUsers")))
                        .during(Integer.parseInt(System.getProperty("rampDuring"))),
                constantUsersPerSec(Integer.parseInt(System.getProperty("endLevelUsers")))
                        .during(Integer.parseInt(System.getProperty("stageDuration")))
        };
    }
}

