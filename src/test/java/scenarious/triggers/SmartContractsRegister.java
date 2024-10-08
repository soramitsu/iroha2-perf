package scenarious.triggers;

import io.gatling.javaapi.core.ScenarioBuilder;
import requests.Triggers;

import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.scenario;

public class SmartContractsRegister {
    public static ScenarioBuilder smartContractsRegister = scenario("register bonds")
            //TODO: create special precondition list
            // must be used onceOnly() controller
            .feed(csv("preconditionList.csv").circular())
            .exec(
                    Triggers.bondAssetTriggerRegister,
                    Triggers.buyBondsTriggerRegister,
                    Triggers.redeemBondsTriggerRegister,
                    Triggers.bondAssetRegister
            );
}
