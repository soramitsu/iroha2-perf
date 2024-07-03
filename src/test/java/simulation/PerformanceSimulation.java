package simulation;

import Configs.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static Configs.Protocol.httpProtocol;
import static scenarious.GeneralQueries.generalQueries;
import static scenarious.HealthCheck.healthCheck;
import static scenarious.TransferAsset.transferAsset;

public class PerformanceSimulation extends Simulation {
    {
        setUp(transferAsset.injectOpen(LoadProfile.getMaxPerformance()).protocols(httpProtocol)).maxDuration(Long.parseLong(System.getProperty("maxDuration")));
    }
}
