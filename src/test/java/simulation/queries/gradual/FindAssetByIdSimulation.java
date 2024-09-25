package simulation.queries.gradual;

import Configs.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static Configs.Protocol.httpProtocol;
import static scenarious.GeneralQueries.findAssetById;

public class FindAssetByIdSimulation extends Simulation {
    {
        setUp(findAssetById.injectOpen(LoadProfile.getMaxPerformanceGradualLoadOpenProfile()).protocols(httpProtocol)).maxDuration(Long.parseLong(System.getProperty("maxDuration")));
    }
}
