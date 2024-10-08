package simulation.transactions.standard;

import configs.gatling.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static configs.gatling.Protocol.httpProtocol;
import static scenarious.GeneralTransaction.txsMultiInstructions;

public class MultiInstructionsSimulation extends Simulation {
    {
        setUp(txsMultiInstructions.injectOpen(LoadProfile.getMaxPerformanceOpenProfile()).protocols(httpProtocol)).maxDuration(Long.parseLong(System.getProperty("maxDuration")));
    }
}
