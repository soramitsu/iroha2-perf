package simulation.smartcontracts.standard;

import Configs.Gatling.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static Configs.Gatling.Protocol.httpProtocol;
import static scenarious.Triggers.RedeemBonds.redeemBonds;

public class RedeemBondsSimulation extends Simulation {
    {
        setUp(redeemBonds.injectOpen(LoadProfile.getMaxPerformanceOpenProfile()).protocols(httpProtocol)).maxDuration(Long.parseLong(System.getProperty("maxDuration")));
    }
}
