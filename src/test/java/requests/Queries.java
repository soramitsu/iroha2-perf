package requests;

import io.gatling.javaapi.core.ChainBuilder;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.generated.SignedQuery;
import jp.co.soramitsu.iroha2.query.QueryBuilder;

import java.util.List;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class Queries extends Constants {

    public static ChainBuilder queryPostFindAllDomains = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(

                    http("findAllDomains query")
                            .post(session -> {return session.getString("peer");})
                            .body(ByteArrayBody(session -> {
                                                return SignedQuery.Companion.encode(QueryBuilder
                                                        .findAllDomains()
                                                        .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                                        .buildSigned(Constants.ALICE_KEYPAIR).getQuery());
                                            }
                                    )
                            )
            ).exec(http("findAllDomains status").get(Constants.URL_STATUS).check(status().is(200)));

    public static ChainBuilder queryPostFindAccountsByDomainId = exec(feed(CSV_FEEDER))
            .exec(
                    http("findAccountsByDomainId query")
                            .post(session -> {return session.getString("peer");})
                            .body(ByteArrayBody(session -> {
                                                return SignedQuery.Companion.encode(QueryBuilder
                                                        .findAccountsByDomainId(ExtensionsKt.asDomainId(session.getString("domainIdSender")))
                                                        .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                                        .buildSigned(Constants.ALICE_KEYPAIR).getQuery());
                                            }
                                    )
                            )

            ).exec(http("findAccountsByDomainId status").get(Constants.URL_STATUS).check(status().is(200)));

    public static ChainBuilder queryPostFindAllAssets = exec(feed(CSV_FEEDER))
            .exec(
                    http("findAllAssets query")
                            .post(session -> {return session.getString("peer");})
                            .body(ByteArrayBody(session -> {
                                                return SignedQuery.Companion.encode(QueryBuilder
                                                        .findAllAssets()
                                                        .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                                        .buildSigned(Constants.ALICE_KEYPAIR).getQuery());
                                            }
                                    )
                            )
            ).exec(http("findAllAssets status").get(Constants.URL_STATUS).check(status().is(200)));

    public static ChainBuilder queryPostFindAllTransactions = exec(feed(CSV_FEEDER))
            .exec(
                    http("findAllTransactions query")
                            .post(session -> {return session.getString("peer");})
                            .body(ByteArrayBody(session -> {
                                                return SignedQuery.Companion.encode(QueryBuilder
                                                        .findAllTransactions(null)
                                                        .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender}")))
                                                        .buildSigned(Constants.ALICE_KEYPAIR).getQuery());
                                            }
                                    )
                            )
            ).exec(http("findAllTransactions status").get(Constants.URL_STATUS).check(status().is(200)));
}
