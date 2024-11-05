package scenarios.triggers;

import io.gatling.javaapi.core.ScenarioBuilder;

import requests.Transactions;
import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.scenario;

public class BuyBonds {

    public static ScenarioBuilder buyBonds = scenario("buy bonds")
            .exec(Transactions.grantPermissionForTriggers)
            .pause(Duration.ofSeconds(5))
            .exec(Transactions.buySomeBondsBondAssetTrigger)
            .pause(Duration.ofSeconds(5))
            .exec(Transactions.triggeringBondAssetSmartContract);
}
