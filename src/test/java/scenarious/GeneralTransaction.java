package scenarious;

import io.gatling.javaapi.core.ScenarioBuilder;
import requests.Transactions;

import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.scenario;

public class GeneralTransaction {

    public static ScenarioBuilder txsMultiInstructions = scenario("multi instructions")
            .feed(csv("preconditionList.csv").circular())
            .exec(Transactions.postRevertWorldViewMultiInstructions)
            .exec(Transactions.postMultiInstructions);

    public static ScenarioBuilder txsTransferAsset = scenario("transfer asset")
            .feed(csv("preconditionList.csv").circular())
            .exec(Transactions.postTransferAsset)
            .exec(Transactions.postTransferAsset)
            .exec(Transactions.postTransferAsset)
            .exec(Transactions.postTransferAsset)
            .exec(Transactions.postTransferAsset);
}
