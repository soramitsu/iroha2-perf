package simulation.smartcontracts.standard;

import configs.gatling.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static configs.gatling.Protocol.httpProtocol;
import static scenarious.triggers.BuyBonds.buyBonds;

public class BuyBondsSimulation extends Simulation {
    {
        setUp(buyBonds.injectOpen(LoadProfile.getMaxPerformanceOpenProfile()).protocols(httpProtocol)).maxDuration(Long.parseLong(System.getProperty("maxDuration")));
    }
}
