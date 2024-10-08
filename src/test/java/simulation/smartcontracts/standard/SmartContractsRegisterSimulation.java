package simulation.smartcontracts.standard;

import configs.gatling.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static configs.gatling.Protocol.httpProtocol;
import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;
import static scenarious.triggers.BuyBonds.buyBonds;
import static scenarious.triggers.RedeemBonds.redeemBonds;
import static scenarious.triggers.SmartContractsRegister.smartContractsRegister;

public class SmartContractsRegisterSimulation extends Simulation {
    {
        setUp(
                smartContractsRegister.injectOpen(atOnceUsers(1))
                        .andThen(buyBonds.injectOpen(LoadProfile.getMaxPerformanceOpenProfile()))
                        .andThen(redeemBonds.injectOpen(atOnceUsers(1)))
        ).protocols(httpProtocol);
    }
}
