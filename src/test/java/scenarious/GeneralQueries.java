package scenarious;

import static io.gatling.javaapi.core.CoreDsl.*;

import io.gatling.javaapi.core.*;
import requests.Transactions;
import requests.Queries;


public class GeneralQueries {

    public static ScenarioBuilder generalQueries = scenario("general queries")
            .feed(csv("preconditionList.csv").circular())
            .exec(Queries.queryPostFindAccountsByDomainId)
            .exec(Queries.queryPostFindAllDomains)
            .exec(Queries.queryPostFindAllAssets)
            .exec(Queries.queryPostFindAccountsByDomainId)
            .exec(Queries.queryPostFindAllDomains)
            .exec(Queries.queryPostFindAllAssets)
            .exec(Queries.queryPostFindAccountsByDomainId)
            .exec(Queries.queryPostFindAllDomains)
            .exec(Queries.queryPostFindAllAssets)
            .exec(Queries.queryPostFindAccountsByDomainId)
            .exec(Queries.queryPostFindAllDomains)
            .exec(Queries.queryPostFindAllAssets)
            .exec(Queries.queryPostFindAccountsByDomainId)
            .exec(Queries.queryPostFindAllDomains)
            .exec(Queries.queryPostFindAllAssets)
            .exec(Queries.queryPostFindAccountsByDomainId)
            .exec(Queries.queryPostFindAllDomains)
            .exec(Queries.queryPostFindAllAssets)
            .exec(Queries.queryPostFindAccountsByDomainId)
            .exec(Queries.queryPostFindAllDomains)
            .exec(Queries.queryPostFindAllAssets)
            .exec(Queries.queryPostFindAccountsByDomainId)
            .exec(Queries.queryPostFindAllDomains)
            .exec(Queries.queryPostFindAllAssets)
            .exec(Queries.queryPostFindAccountsByDomainId)
            .exec(Queries.queryPostFindAllDomains)
            .exec(Queries.queryPostFindAllAssets)
            .exec(Queries.queryPostFindAccountsByDomainId)
            .exec(Queries.queryPostFindAllDomains)
            .exec(Queries.queryPostFindAllAssets)
            .exec(Queries.queryPostFindAccountsByDomainId)
            .exec(Queries.queryPostFindAllDomains)
            .exec(Queries.queryPostFindAllAssets)
            .exec(Queries.queryPostFindAccountsByDomainId)
            .exec(Queries.queryPostFindAllDomains)
            .exec(Queries.queryPostFindAllAssets)
            .exec(Queries.queryPostFindAccountsByDomainId)
            .exec(Queries.queryPostFindAllDomains)
            .exec(Queries.queryPostFindAllAssets)
            .exec(Queries.queryPostFindAccountsByDomainId)
            .exec(Queries.queryPostFindAllDomains)
            .exec(Queries.queryPostFindAllAssets)
            .exec(Queries.queryPostFindAccountsByDomainId)
            .exec(Queries.queryPostFindAllDomains)
            .exec(Queries.queryPostFindAllAssets)
            .exec(Queries.queryPostFindAccountsByDomainId)
            .exec(Queries.queryPostFindAllDomains)
            .exec(Queries.queryPostFindAllAssets)
            .exec(Queries.queryPostFindAccountsByDomainId)
            .exec(Queries.queryPostFindAllDomains)
            .exec(Queries.queryPostFindAllAssets)
            .exec(Queries.queryPostFindAccountsByDomainId)
            .exec(Queries.queryPostFindAllDomains)
            .exec(Queries.queryPostFindAllAssets)
            .exec(Queries.queryPostFindAccountsByDomainId);
}
