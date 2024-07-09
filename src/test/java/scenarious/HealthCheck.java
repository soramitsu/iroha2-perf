package scenarious;

import io.gatling.javaapi.core.ScenarioBuilder;
import requests.Queries;

import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.scenario;


public class HealthCheck {

    public static ScenarioBuilder healthCheck = scenario("health check")
            .feed(csv("preconditionList.csv").circular())
            .exec(
                    Queries.healthCheck
            );

}
