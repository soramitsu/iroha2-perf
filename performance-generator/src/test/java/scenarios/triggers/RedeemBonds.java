package scenarios.triggers;

import io.gatling.javaapi.core.ScenarioBuilder;

import requests.Transactions;
import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.scenario;

public class RedeemBonds {
    public static ScenarioBuilder redeemBonds = scenario("redeem bonds")
            .exec(Transactions.redeemBondsBondAssetTrigger)
            .pace(Duration.ofSeconds(5));
}
