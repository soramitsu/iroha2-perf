package simulation;

import Configs.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static Configs.Protocol.httpProtocol;
import static scenarious.GeneralQueries.*;
import static scenarious.GeneralTransaction.*;
import static scenarious.HealthCheck.*;
import static scenarious.DefinitionId.*;
import static scenarious.TransferAsset.*;

public class PerformanceSimulation extends Simulation {
    {
        setUp(txsMultiInstructions.injectOpen(LoadProfile.getMaxPerformance()).protocols(httpProtocol)).maxDuration(Long.parseLong(System.getProperty("maxDuration")));
    }
}
