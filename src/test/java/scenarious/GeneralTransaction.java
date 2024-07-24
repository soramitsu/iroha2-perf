package scenarious;

import io.gatling.javaapi.core.ScenarioBuilder;
import requests.Transactions;

import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.scenario;

public class GeneralTransaction {
    public static ScenarioBuilder findAllAccounts = scenario("many instructions")
            .feed(csv("preconditionList.csv").circular())
            .exec(Transactions.postMultiInstructions)
            .exec(Transactions.postRevertWorldViewMultiInstructions);
}
