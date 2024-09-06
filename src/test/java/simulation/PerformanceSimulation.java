package simulation;

import Configs.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static Configs.Protocol.httpProtocol;
import static scenarious.GeneralQueries.*;
import static scenarious.irohaClient.BatchQueries.*;
import static scenarious.GeneralTransaction.*;
import static scenarious.GeneralQueries.*;
import static scenarious.HealthCheck.*;
import static scenarious.DefinitionId.*;
import static scenarious.TransferAsset.*;

public class PerformanceSimulation extends Simulation {
    {
        setUp(findAllAssetClientMode.injectOpen(LoadProfile.getMaxPerformanceOpenProfile()).protocols(httpProtocol)).maxDuration(Long.parseLong(System.getProperty("maxDuration")));
    }
}
