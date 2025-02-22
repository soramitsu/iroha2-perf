package scenarios;

import io.gatling.javaapi.core.ScenarioBuilder;

import requests.Transactions;

import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.scenario;

public class TransferAsset {

    public static ScenarioBuilder transferAsset = scenario("transfer asset")
            .feed(csv("preconditionList.csv").circular())
            .exec(
                    Transactions.postTransferAsset
            );
}
