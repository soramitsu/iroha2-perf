package irohatestadditional;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import jp.co.soramitsu.iroha2.CryptoUtils;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.generated.AccountId;
import jp.co.soramitsu.iroha2.generated.DomainId;
import jp.co.soramitsu.iroha2.generated.SignedQuery;
import jp.co.soramitsu.iroha2.query.QueryBuilder;

import java.security.KeyPair;


public class IrohaUserGetBalanceSimulation extends Simulation {
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
  
	
	ChainBuilder get3 = exec(http("Get 2 query")
			.post("/peer-2/query")
			.body(ByteArrayBody(SignedQuery.Companion.encode(QueryBuilder
					.findAllDomains()
					.account(ALICE_ACCOUNT_ID)
					.buildSigned(ALICE_KEYPAIR).getQuery())))
			);


	ChainBuilder getAll = exec(
			//http("Get 0 status").get("/peer-0/status").check(status().is(200)), pause(1),
		//	http("Get 1 status").get("/peer-1/status").check(status().is(200)), pause(1),
		//	http("Get 2 status").get("/peer-2/status").check(status().is(200)), pause(1),
			http("Get 2 status").get("/peer-2/status").check(status().is(200)))
			// if the chain didn't finally succeed, have the user exit the whole scenario
			.exitHereIfFailed();

	HttpProtocolBuilder httpProtocol = http.baseUrl("https://iroha2.test2.tachi.soramitsu.co.jp")
			.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			.acceptLanguageHeader("en-US,en;q=0.5")
			.basicAuth("iroha2-test", "7kUHkgq30JBeVyJVZ4Z1wbGBP3vah3");

	ScenarioBuilder users = scenario("Users").exec(get3, getAll);

	{
		setUp(users.injectOpen(rampUsers(1).during(1))).protocols(httpProtocol);
	}

}
