package requests;

import io.gatling.javaapi.core.ChainBuilder;
import jp.co.soramitsu.iroha2.CryptoUtils;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.generated.SignedQuery;
import jp.co.soramitsu.iroha2.query.QueryBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class Queries extends Constants {

    /*
    Тяжёлые query
    FindAllAccounts
    FindAccountsByDomainId
    FindAllAssets
    FindAllAssetsDefinitions
    FindAssetsByDomainId

    Лёгкие query
    FindAccountById
    FindAssetById
    FindAssetDefinitionById
    FindAssetQuantityById
    FindTotalAssetQuantityByAssetDefinitionId
    FindDomainById
    */

    public static ChainBuilder healthCheck = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(http("health check")
                    .get(session -> {
                        return session.getString("peer") + "health";
                    }));

    public static ChainBuilder queryPostFindAllAssetsDefinitions = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(http("findAllAssetsDefinitions")
                    .post(session -> {
                        return session.getString("peer") + URL_QUERY;
                    })
                    .body(ByteArrayBody(session -> {
                        return SignedQuery.Companion.encode(QueryBuilder
                                .findAllAssetsDefinitions()
                                .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                .buildSigned(CryptoUtils.keyPairFromHex(
                                        session.getString("publicKeySender"),
                                        session.getString("privateKeySender")
                                )).getQuery());
                    }))
            );

    public static ChainBuilder queryPostFindAssetsByDomainId = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(http("findAssetsByDomainId")
                    .post(session -> {
                        return session.getString("peer") + URL_QUERY;
                    })
                    .body(ByteArrayBody(session -> {
                        return SignedQuery.Companion.encode(QueryBuilder
                                .findAssetsByDomainId(ExtensionsKt.asDomainId(session.getString("domainIdSender")))
                                .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                .buildSigned(CryptoUtils.keyPairFromHex(
                                        session.getString("publicKeySender"),
                                        session.getString("privateKeySender")
                                )).getQuery());
                    }))
            );

    public static ChainBuilder queryPostFindAccountById = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(http("findAccountById")
                    .post(session -> {
                        return session.getString("peer") + URL_QUERY;
                    })
                    .body(ByteArrayBody(session -> {
                        return SignedQuery.Companion.encode(QueryBuilder
                                .findAccountById(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdReceiver")))
                                .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                .buildSigned(CryptoUtils.keyPairFromHex(
                                        session.getString("publicKeySender"),
                                        session.getString("privateKeySender")
                                )).getQuery());
                    }))
            );

    public static ChainBuilder queryPostFindAssetById = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(http("findAssetById")
                    .post(session -> {
                        return session.getString("peer") + URL_QUERY;
                    })
                    .body(ByteArrayBody(session -> {
                        return SignedQuery.Companion.encode(QueryBuilder
                                .findAssetById(ExtensionsKt.asAssetId(session.getString("anotherDevAssetIdSender")))
                                .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                .buildSigned(CryptoUtils.keyPairFromHex(
                                        session.getString("publicKeySender"),
                                        session.getString("privateKeySender")
                                )).getQuery());
                    }))
            );

    //TODO: add to preconditionList.csv definitionId for findAssetDefinitionById()
    public static ChainBuilder queryPostFindAssetDefinitionById = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(http("findAssetDefinitionById")
                    .post(session -> {
                        return session.getString("peer") + URL_QUERY;
                    })
                    .body(ByteArrayBody(session -> {
                        return SignedQuery.Companion.encode(QueryBuilder
                                .findAssetDefinitionById(ExtensionsKt.asAssetDefinitionId(session.getString("")))
                                .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                .buildSigned(CryptoUtils.keyPairFromHex(
                                        session.getString("publicKeySender"),
                                        session.getString("privateKeySender")
                                )).getQuery());
                    }))
            );

    public static ChainBuilder queryPostFindAssetQuantityById = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(http("findAssetQuantityById")
                    .post(session -> {
                        return session.getString("peer") + URL_QUERY;
                    })
                    .body(ByteArrayBody(session -> {
                        return SignedQuery.Companion.encode(QueryBuilder
                                .findAssetQuantityById(ExtensionsKt.asAssetId(session.getString("anotherDevAssetIdSender")))
                                .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                .buildSigned(CryptoUtils.keyPairFromHex(
                                        session.getString("publicKeySender"),
                                        session.getString("privateKeySender")
                                )).getQuery());
                    }))
            );

    public static ChainBuilder queryPostFindAllDomains = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(http("findAllDomains")
                    .post(session -> {
                        return session.getString("peer") + URL_QUERY;
                    })
                    .body(ByteArrayBody(session -> {
                        return SignedQuery.Companion.encode(QueryBuilder
                                .findAllDomains()
                                .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                .buildSigned(CryptoUtils.keyPairFromHex(
                                        session.getString("publicKeySender"),
                                        session.getString("privateKeySender")))
                                .getQuery());
                    }))
            );

    public static ChainBuilder queryPostFindAccountsByDomainId = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(
                    http("findAccountsByDomainId")
                            .post(session -> {
                                        return session.getString("peer") + URL_QUERY;
                                    }
                            )
                            .body(ByteArrayBody(session -> {
                                return SignedQuery.Companion.encode(QueryBuilder
                                        .findAccountsByDomainId(ExtensionsKt.asDomainId(session.getString("domainIdSender")))
                                        .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                        .buildSigned(CryptoUtils.keyPairFromHex(
                                                session.getString("publicKeySender"),
                                                session.getString("privateKeySender")))
                                        .getQuery());
                            }))
            );

    public static ChainBuilder queryPostFindAllAssets = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(
                    http("findAllAssets")
                            .post(session -> {
                                        return session.getString("peer") + URL_QUERY;
                                    }
                            )
                            .body(ByteArrayBody(session -> {
                                return SignedQuery.Companion.encode(QueryBuilder
                                        .findAllAssets()
                                        .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                        .buildSigned(CryptoUtils.keyPairFromHex(
                                                session.getString("publicKeySender"),
                                                session.getString("privateKeySender")))
                                        .getQuery());
                            }))
            );

    public static ChainBuilder queryPostFindAllAccounts = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(
                    http("findAllAccounts")
                            .post(session -> {
                                        return session.getString("peer") + URL_QUERY;
                                    }
                            )
                            .body(ByteArrayBody(session -> {
                                return SignedQuery.Companion.encode(QueryBuilder
                                        .findAllAccounts()
                                        .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                        .buildSigned(CryptoUtils.keyPairFromHex(
                                                session.getString("publicKeySender"),
                                                session.getString("privateKeySender")))
                                        .getQuery());
                            }))
            );

    public static ChainBuilder queryPostFindAllTransactions = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(
                    http("findAllTransactions")
                            .post(session -> {
                                        return session.getString("peer") + URL_QUERY;
                                    }
                            )
                            .body(ByteArrayBody(session -> {
                                return SignedQuery.Companion.encode(QueryBuilder
                                        .findAllTransactions(null)
                                        .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                        .buildSigned(CryptoUtils.keyPairFromHex(
                                                session.getString("publicKeySender"),
                                                session.getString("privateKeySender")))
                                        .getQuery());
                            }))
            );
}
