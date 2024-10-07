package simulation.queries.standard;

import Configs.Gatling.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static Configs.Gatling.Protocol.httpProtocol;
import static scenarious.GeneralQueries.findAssetById;

public class FindAssetByIdSimulation extends Simulation {
    {
        setUp(findAssetById.injectOpen(LoadProfile.getMaxPerformanceOpenProfile()).protocols(httpProtocol)).maxDuration(Long.parseLong(System.getProperty("maxDuration")));
    }
}
