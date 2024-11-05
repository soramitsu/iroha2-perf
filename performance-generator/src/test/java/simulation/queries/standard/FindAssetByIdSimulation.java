package simulation.queries.standard;

import config.gatling.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static config.gatling.Protocol.httpProtocol;
import static scenarios.GeneralQueries.findAssetById;

public class FindAssetByIdSimulation extends Simulation {
    {
<<<<<<< Updated upstream
        setUp(findAssetById.injectOpen(LoadProfile.getMaxPerformance()).protocols(httpProtocol)).maxDuration(Long.parseLong(System.getProperty("maxDuration")));
=======
        setUp(findAssetById.injectOpen(LoadProfile.getMaxPerformanceOpenProfile()).protocols(httpProtocol)).maxDuration(Long.parseLong(System.getProperty("maxDuration")));
>>>>>>> Stashed changes
    }
}
