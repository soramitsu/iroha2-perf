package simulation.smartcontracts.standard;

import config.gatling.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static config.gatling.Protocol.httpProtocol;
import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;
import static scenarios.triggers.BuyBonds.buyBonds;
import static scenarios.triggers.RedeemBonds.redeemBonds;
import static scenarios.triggers.RegisterSmartContracts.registerBondAsset;
import static scenarios.triggers.RegisterSmartContracts.registerSmartContracts;

public class SmartContractsRegisterSimulation extends Simulation {
    {
        setUp(
                registerSmartContracts.injectOpen(atOnceUsers(1))
                        .andThen(registerBondAsset.injectOpen(atOnceUsers(1))) // 1 bonds on each domain
                        .andThen(buyBonds.injectOpen(LoadProfile.getMaxPerformanceOpenProfile()))
                        .andThen(redeemBonds.injectOpen(atOnceUsers(1)))
        ).protocols(httpProtocol);
    }
}
