package simulation.smartcontracts.standard;

import configs.gatling.LoadProfile;
import io.gatling.javaapi.core.Simulation;

import static configs.gatling.Protocol.httpProtocol;
import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;
import static scenarious.triggers.BuyBonds.buyBonds;
import static scenarious.triggers.RedeemBonds.redeemBonds;
import static scenarious.triggers.RegisterSmartContracts.*;

public class SmartContractsRegisterSimulation extends Simulation{
    {
        setUp(
                /*registerSmartContracts.injectOpen(atOnceUsers(1))
                        //несколько бондов на 1 доме. колличество бондов в каждом домене может быть равным. создай не менее 5 на каждый домен
                        .andThen(*/registerBondAsset.injectOpen(atOnceUsers(1))/*)*/ // 5 bonds on each domain
                        //
                        /*.andThen(buyBonds.injectOpen(LoadProfile.getMaxPerformanceOpenProfile()))
                        .andThen(redeemBonds.injectOpen(atOnceUsers(1)))*/
        ).protocols(httpProtocol);
    }
}
