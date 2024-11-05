package simulation.transactions.gradual;

import config.gatling.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static config.gatling.Protocol.httpProtocol;
import static scenarios.GeneralTransaction.txsMultiInstructions;

public class MultiInstructionsSimulation extends Simulation {
    {
        setUp(txsMultiInstructions.injectOpen(LoadProfile.getMaxPerformanceGradualLoadOpenProfile()).protocols(httpProtocol)).maxDuration(Long.parseLong(System.getProperty("maxDuration")));
    }
}
