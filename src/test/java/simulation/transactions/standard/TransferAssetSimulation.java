package simulation.transactions.standard;

import Configs.Gatling.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static Configs.Gatling.Protocol.httpProtocol;
import static scenarious.GeneralTransaction.txsTransferAsset;

public class TransferAssetSimulation extends Simulation {
    {
        setUp(txsTransferAsset.injectOpen(LoadProfile.getMaxPerformanceOpenProfile()).protocols(httpProtocol)).maxDuration(Long.parseLong(System.getProperty("maxDuration")));
    }
}
