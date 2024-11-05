package scenarios;

import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.ScenarioBuilder;

import requests.Queries;

import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.scenario;

public class GeneralQueries {

    public static ScenarioBuilder findAllAssets = scenario("find all assets")
            .feed(csv("preconditionList.csv").circular())
            .repeat(10).on(
                    CoreDsl.exec(Queries.queryPostFindAllAssets)
            );

    public static ScenarioBuilder findAssetById = scenario("find asset by id")
            .feed(csv("preconditionList.csv").circular())
            .repeat(10).on(
                    CoreDsl.exec(Queries.queryPostFindAssetById)
            );
}
