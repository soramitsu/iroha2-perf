package scenarious.triggers;

import io.gatling.javaapi.core.ScenarioBuilder;
import requests.Transactions;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.scenario;

public class RedeemBonds {
    public static ScenarioBuilder redeemBonds = scenario("redeem bonds")
            /*.feed(csv("preconditionList.csv").circular())*/
            .exec(Transactions.redeemBondsBondAssetTrigger)
            .pace(Duration.ofSeconds(5));
}
