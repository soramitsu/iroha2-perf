package simulation.transactions.gradual;

import Configs.Gatling.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static Configs.Gatling.Protocol.httpProtocol;
import static scenarious.GeneralTransaction.txsMultiInstructions;

public class MultiInstructionsSimulation extends Simulation {
    {
        setUp(txsMultiInstructions.injectOpen(LoadProfile.getMaxPerformanceGradualLoadOpenProfile()).protocols(httpProtocol)).maxDuration(Long.parseLong(System.getProperty("maxDuration")));
    }
}
