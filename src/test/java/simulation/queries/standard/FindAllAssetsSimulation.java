package simulation.queries.standard;

import Configs.Gatling.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static Configs.Gatling.Protocol.httpProtocol;
import static scenarious.GeneralQueries.findAllAssets;

public class FindAllAssetsSimulation extends Simulation {
    {
        setUp(findAllAssets.injectOpen(LoadProfile.getMaxPerformanceOpenProfile()).protocols(httpProtocol)).maxDuration(Long.parseLong(System.getProperty("maxDuration")));
    }
}
