package irohatestadditional;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import jp.co.soramitsu.iroha2.CryptoUtils;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.generated.*;
import jp.co.soramitsu.iroha2.query.QueryBuilder;
import jp.co.soramitsu.iroha2.transaction.TransactionBuilder;

import java.security.KeyPair;
import java.util.UUID;


public class IrohaUserGetBalanceSimulation extends Simulation {
    public static final String URL_QUERY = "/peer-2/query";
    public static final String URL_STATUS = "/peer-2/status";
    public static final String URL_TRANSACTION = "/peer-2/api/transaction";
    public static final String DEFAULT_DOMAIN = "wonderland";
    public static final String ALICE_ACCOUNT = "alice";
    public static final String BOB_ACCOUNT = "bob";
    public static final String GENESIS = "genesis";
    public static final String ALICE_ACCOUNT_ID_VALUE = "alice@wonderland";
    public static final AccountId ALICE_ACCOUNT_ID = ExtensionsKt.asAccountId(ALICE_ACCOUNT_ID_VALUE);
    public static final DomainId WONDERLAND_DOMAIN_ID = ExtensionsKt.asDomainId(DEFAULT_DOMAIN);
    public static final KeyPair ALICE_KEYPAIR = CryptoUtils.keyPairFromHex(
            "7233bfc89dcbd68c19fde6ce6158225298ec1131b6a130d1aeb454c1ab5183c0",
            "9ac47abf59b356e0bd7dcbbbb4dec080e302156a48ca907e47cb6aea1d32719e");

	final DomainId domainId = new DomainId(new Name("new_domain_name"));
    public static final UUID CHAIN_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    ChainBuilder queryPostFindAllDomains = exec(http("findAllDomains query")
            .post(URL_QUERY)
            .body(ByteArrayBody(SignedQuery.Companion.encode(QueryBuilder
                    .findAllDomains()
                    .account(ALICE_ACCOUNT_ID)
                    .buildSigned(ALICE_KEYPAIR).getQuery())))
    ).exec(http("findAllDomains status").get(URL_STATUS).check(status().is(200)));

    ChainBuilder queryPostFindAccountsByDomainId = exec(http("findAccountsByDomainId query")
            .post(URL_QUERY)
            .body(ByteArrayBody(SignedQuery.Companion.encode(QueryBuilder
                    .findAccountsByDomainId(WONDERLAND_DOMAIN_ID)
                    .account(ALICE_ACCOUNT_ID)
                    .buildSigned(ALICE_KEYPAIR).getQuery())))
    ).exec(http("findAccountsByDomainId status").get(URL_STATUS).check(status().is(200)));

    ChainBuilder queryPostFindAllAssets = exec(http("findAllAssets query")
            .post(URL_QUERY)
            .body(ByteArrayBody(SignedQuery.Companion.encode(QueryBuilder
                    .findAllAssets()
                    .account(ALICE_ACCOUNT_ID)
                    .buildSigned(ALICE_KEYPAIR).getQuery())))
    ).exec(http("findAllAssets status").get(URL_STATUS).check(status().is(200)));

    ChainBuilder queryPostFindAllTransactions = exec(http("findAllTransactions query")
            .post(URL_QUERY)
            .body(ByteArrayBody(SignedQuery.Companion.encode(QueryBuilder
                    .findAllTransactions(null)
                    .account(ALICE_ACCOUNT_ID)
                    .buildSigned(ALICE_KEYPAIR).getQuery())))
    ).exec(http("findAllAssets status").get(URL_STATUS).check(status().is(200)));

    ChainBuilder txPostRegisterDomain = exec(http("registerDomain transaction")
            .post(URL_TRANSACTION)
            .body(ByteArrayBody(
					SignedTransaction.Companion.encode(
							TransactionBuilder.Companion.builder()
									.account(ALICE_ACCOUNT_ID)
                                    .chainId(CHAIN_ID)
									.registerDomain(domainId)
									.buildSigned(ALICE_KEYPAIR))))
    ).exec(http("registerDomain status").get(URL_STATUS).check(status().is(200)));

    HttpProtocolBuilder httpProtocol = http.baseUrl("https://iroha2.test2.tachi.soramitsu.co.jp")
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .acceptLanguageHeader("en-US,en;q=0.5")
            .basicAuth("iroha2-test", "7kUHkgq30JBeVyJVZ4Z1wbGBP3vah3");

    ScenarioBuilder users = scenario("Users")
            .exec(queryPostFindAllDomains
                    , queryPostFindAccountsByDomainId
                    , queryPostFindAllAssets
                    , queryPostFindAllTransactions);

    {
        setUp(users.injectOpen(rampUsers(1).during(1))).protocols(httpProtocol);
    }

}
