package simulation.smartcontracts.standard;

import Configs.Gatling.LoadProfile;
import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.Simulation;

import java.util.List;

import static Configs.Gatling.Protocol.httpProtocol;
import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;
import static scenarious.Triggers.BuyBonds.buyBonds;
import static scenarious.Triggers.RedeemBonds.redeemBonds;
import static scenarious.Triggers.SmartContractsRegister.smartContractsRegister;

public class SmartContractsRegisterSimulation extends Simulation {
    {
        setUp(
                smartContractsRegister.injectOpen(atOnceUsers(1))
                        .andThen(buyBonds.injectOpen(LoadProfile.getMaxPerformanceOpenProfile()))
                        .andThen(redeemBonds.injectOpen(atOnceUsers(1)))
        ).protocols(httpProtocol);
    }
}
