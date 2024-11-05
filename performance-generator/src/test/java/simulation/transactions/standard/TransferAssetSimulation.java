package simulation.transactions.standard;

import config.gatling.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static config.gatling.Protocol.httpProtocol;
import static scenarios.GeneralTransaction.txsTransferAsset;

public class TransferAssetSimulation extends Simulation {
    {
<<<<<<< Updated upstream
        setUp(txsTransferAsset.injectOpen(LoadProfile.getMaxPerformance()).protocols(httpProtocol)).maxDuration(Long.parseLong(System.getProperty("maxDuration")));
=======
        setUp(txsTransferAsset.injectOpen(LoadProfile.getMaxPerformanceOpenProfile()).protocols(httpProtocol)).maxDuration(Long.parseLong(System.getProperty("maxDuration")));
>>>>>>> Stashed changes
    }
}
