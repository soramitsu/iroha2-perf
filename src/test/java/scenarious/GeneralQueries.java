package scenarious;

import static io.gatling.javaapi.core.CoreDsl.*;

import io.gatling.javaapi.core.*;
import requests.Transactions;
import requests.Queries;


public class GeneralQueries {

    public static ScenarioBuilder generalQueries = scenario("general queries")
            .feed(csv("preconditionList.csv").circular())
            .exec(Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets,
                    Queries.queryPostFindAllDomains,
                    Queries.queryPostFindAccountsByDomainId,
                    Queries.queryPostFindAllAssets);

}
