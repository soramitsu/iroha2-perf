package scenarious.triggers;

import io.gatling.javaapi.core.ScenarioBuilder;
import requests.Transactions;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.scenario;

public class BuyBonds {

    public static ScenarioBuilder buyBonds = scenario("buy bonds")
            .feed(csv("preconditionList.csv").circular())
            .exec(Transactions.grantPermissionForTriggers)
            //.pause(Duration.ofSeconds(10))
            .exec(Transactions.buySomeBondsBondAssetTrigger)
            //.pause(Duration.ofSeconds(10))
            .exec(Transactions.triggeringBondAssetSmartContract);
}
