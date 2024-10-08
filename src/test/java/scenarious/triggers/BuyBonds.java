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
            .pace(Duration.ofSeconds(10))
            .exec(Transactions.buySomeBondsBondAssetTrigger)
            .pace(Duration.ofSeconds(5))
            .exec(Transactions.triggeringBondAssetSmartContract)
            .pace(Duration.ofSeconds(5));
            //смотри L460 iroha2-adapter proj
            //нужны квери до buySomeBondsBondAssetTrigger -> получение баланса по текущему пользователю
            //и после triggeringBondAssetSmartContract для верификации
}
