package scenarious.triggers;

import io.gatling.javaapi.core.ScenarioBuilder;
import requests.Triggers;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.scenario;

public class RegisterSmartContracts {

    //TODO: create special precondition list
    // must be used onceOnly() controller
    public static ScenarioBuilder registerSmartContracts = scenario("register smart contracts")
            .feed(csv("preconditionList.csv").circular())
            .exec(
                    Triggers.bondAssetTriggerRegister,
                    Triggers.buyBondsTriggerRegister,
                    Triggers.redeemBondsTriggerRegister
            )
            .pace(Duration.ofSeconds(15))
            .exec(
                    Triggers.bondAssetRegister
            );
}
