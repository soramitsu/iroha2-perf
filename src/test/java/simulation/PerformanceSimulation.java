package simulation;

import Configs.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static Configs.Protocol.httpProtocol;
import static scenarious.GeneralQueries.generalQueries;

public class PerformanceSimulation extends Simulation {
    {
        setUp(generalQueries.injectOpen(LoadProfile.getMaxPerformance()).protocols(httpProtocol));
    }
}
