package scenarios;

import io.gatling.javaapi.core.ScenarioBuilder;

import requests.Transactions;

import static io.gatling.javaapi.core.CoreDsl.scenario;

public class DefinitionId {
    public static ScenarioBuilder definitionId = scenario("register definition id")
            .exec(
                    Transactions.postRegisterDefinitionId
            );
}
