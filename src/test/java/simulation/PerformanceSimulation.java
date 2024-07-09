package simulation;

import Configs.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static Configs.Protocol.httpProtocol;
import static scenarious.GeneralQueries.*;
import static scenarious.HealthCheck.*;
import static scenarious.DefinitionId.*;

public class PerformanceSimulation extends Simulation {
    {
        setUp(definitionId.injectOpen(LoadProfile.getMaxPerformance()).protocols(httpProtocol)).maxDuration(Long.parseLong(System.getProperty("maxDuration")));
    }
}
