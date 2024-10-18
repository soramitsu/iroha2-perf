package simulation.smartcontracts.gradual;

import configs.gatling.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static configs.gatling.Protocol.httpProtocol;
import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;
import static scenarious.triggers.BuyBonds.buyBonds;
import static scenarious.triggers.RedeemBonds.redeemBonds;
import static scenarious.triggers.RegisterSmartContracts.registerBondAsset;
import static scenarious.triggers.RegisterSmartContracts.registerSmartContracts;

public class SmartContractsRegisterSimulation extends Simulation{
    {
        setUp(
                registerSmartContracts.injectOpen(atOnceUsers(1))
                        .andThen(registerBondAsset.injectOpen(atOnceUsers(1))) // 1 bonds on each domain
                        .andThen(buyBonds.injectOpen(LoadProfile.getMaxPerformanceGradualLoadOpenProfile()))
                        .andThen(redeemBonds.injectOpen(atOnceUsers(1)))
        ).protocols(httpProtocol);
    }
}
