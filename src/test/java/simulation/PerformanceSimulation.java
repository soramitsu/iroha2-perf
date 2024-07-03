package simulation;

import Configs.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static Configs.Protocol.httpProtocol;
import static scenarious.GeneralQueries.generalQueries;
import static scenarious.HealthCheck.healthCheck;

public class PerformanceSimulation extends Simulation {
    {
        setUp(healthCheck.injectOpen(LoadProfile.getMaxPerformance()).protocols(httpProtocol)).maxDuration(Long.parseLong(System.getProperty("maxDuration")));
    }
}
