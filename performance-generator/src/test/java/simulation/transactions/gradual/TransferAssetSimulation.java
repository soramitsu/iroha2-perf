package simulation.transactions.gradual;

import config.gatling.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static config.gatling.Protocol.httpProtocol;
import static scenarios.GeneralTransaction.txsTransferAsset;

public class TransferAssetSimulation extends Simulation {
    {
        setUp(txsTransferAsset.injectOpen(LoadProfile.getMaxPerformanceGradualLoadOpenProfile()).protocols(httpProtocol)).maxDuration(Long.parseLong(System.getProperty("maxDuration")));
    }
}
