package simulation.queries.gradual;

import configs.gatling.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static configs.gatling.Protocol.httpProtocol;
import static scenarios.GeneralQueries.findAllAssets;

public class FindAllAssetsSimulation extends Simulation {
    {
        setUp(findAllAssets.injectOpen(LoadProfile.getMaxPerformanceGradualLoadOpenProfile()).protocols(httpProtocol)).maxDuration(Long.parseLong(System.getProperty("maxDuration")));
    }
}
