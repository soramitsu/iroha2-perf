package simulation.transactions.standard;

import config.gatling.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static config.gatling.Protocol.httpProtocol;
import static scenarios.GeneralTransaction.txsMultiInstructions;

public class MultiInstructionsSimulation extends Simulation {
    {
<<<<<<< Updated upstream
        setUp(txsMultiInstructions.injectOpen(LoadProfile.getMaxPerformance()).protocols(httpProtocol)).maxDuration(Long.parseLong(System.getProperty("maxDuration")));
=======
        setUp(txsMultiInstructions.injectOpen(LoadProfile.getMaxPerformanceOpenProfile()).protocols(httpProtocol)).maxDuration(Long.parseLong(System.getProperty("maxDuration")));
>>>>>>> Stashed changes
    }
}
