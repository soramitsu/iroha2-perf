package simulation.queries.standard;

import configs.gatling.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static configs.gatling.Protocol.httpProtocol;
import static scenarious.GeneralQueries.findAllAssets;

public class FindAllAssetsSimulation extends Simulation {
    {
        setUp(findAllAssets.injectOpen(LoadProfile.getMaxPerformanceOpenProfile()).protocols(httpProtocol)).maxDuration(Long.parseLong(System.getProperty("maxDuration")));
    }
}
