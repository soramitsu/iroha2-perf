package simulation.queries.gradual;

import Configs.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static Configs.Protocol.httpProtocol;
import static scenarious.GeneralQueries.findAllAssets;

public class FindAllAssetsSimulation extends Simulation {
    {
        setUp(findAllAssets.injectOpen(LoadProfile.getMaxPerformanceGradualLoadOpenProfile()).protocols(httpProtocol)).maxDuration(Long.parseLong(System.getProperty("maxDuration")));
    }
}
