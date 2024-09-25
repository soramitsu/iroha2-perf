package requests;

import io.gatling.javaapi.core.ChainBuilder;
import jp.co.soramitsu.iroha2.CryptoUtils;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.generated.*;
import jp.co.soramitsu.iroha2.query.QueryAndExtractor;
import jp.co.soramitsu.iroha2.query.QueryBuilder;

import java.util.List;
import java.util.ArrayList;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class Queries extends Constants {

    /*
    heavy query
    FindAllAccounts
    FindAccountsByDomainId
    FindAllAssets
    FindAllAssetsDefinitions
    FindAssetsByDomainId

    lite query
    FindAccountById
    FindAssetById
    FindAssetDefinitionById
    FindAssetQuantityById
    FindTotalAssetQuantityByAssetDefinitionId
    FindDomainById
    */

    private static QueryAndExtractor queryFindAllAsset;
    private static BatchedResponse<QueryOutputBox> batchedResponse;
    private static BatchedResponse.V1 batchedResponseV1;
    private static List<QueryOutputBox> resultList = new ArrayList<>();
    private static ForwardCursor cursor;

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

    public static ChainBuilder paginatedQueryPostFindAllAssets = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(session -> {
                        queryFindAllAsset = QueryBuilder
                                .findAllAssets()
                                .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                .buildSigned(CryptoUtils.keyPairFromHex(
                                        session.getString("publicKeySender"),
                                        session.getString("privateKeySender")));
                        return session;
                    }
            )
            .exec(http("checkBatchesQuery")
                    .post(session -> {
                                return session.getString("peer") + URL_QUERY;
                            }
                    )
                    .body(ByteArrayBody(session -> {
                                        var response = SignedQuery.Companion.encode(queryFindAllAsset.getQuery());
                                        BatchedResponse rawResponse = BatchedResponse.Companion.decode(response);
                                        if (rawResponse instanceof BatchedResponse) {
                                            batchedResponse = (BatchedResponse<QueryOutputBox>) rawResponse;
                                        }
                                        cursor = ((BatchedResponse.V1) batchedResponse).getBatchedResponseV1().getCursor();
                                        session.set("conditionCursor", ((BatchedResponse.V1) batchedResponse).getBatchedResponseV1().getCursor().toString());
                                        return null;
                                    }
                            )
                    )
            )
            .doWhile(session -> session.get("conditionCursor"))
            .on(http("additionQuery")
                            .post("")
                            .queryParam("query", cursor.getQuery())
                            .queryParam("cursor", cursor.getCursor().getU64())
                            .body(ByteArrayBody(session -> {
                                                var response = SignedQuery.Companion.encode(queryFindAllAsset.getQuery());
                                                BatchedResponse rawResponse = BatchedResponse.Companion.decode(response);
                                                if (rawResponse instanceof BatchedResponse) {
                                                    batchedResponse = (BatchedResponse<QueryOutputBox>) rawResponse;
                                                }
                                                batchedResponseV1 = (BatchedResponse.V1) batchedResponse;
                                                resultList.addAll(
                                                        (List<QueryOutputBox>) batchedResponseV1.component1().getBatch()
                                                );
                                                cursor = ((BatchedResponse.V1) batchedResponse).getBatchedResponseV1().getCursor();
                                                session.set("conditionCursor", ((BatchedResponse.V1) batchedResponse).getBatchedResponseV1().getCursor().toString());
                                                return null;
                                            }
                                    )
                            )
            )
            /*.exec(session -> {
                        byte[] responseBodyByte = session.get("fullResponse");
                        try {
                            BatchedResponse rawResponse = BatchedResponse.Companion.decode(responseBodyByte);
                            if (rawResponse instanceof BatchedResponse) {
                                batchedResponse = (BatchedResponse<QueryOutputBox>) rawResponse;
                            }
                        } catch (ClassCastException ex) {
                            ex.getMessage();
                        }
                        batchedResponseV1 = (BatchedResponse.V1) batchedResponse;
                        resultList.addAll(
                                (List<QueryOutputBox>) batchedResponseV1.component1().getBatch()
                        );
                        var batch = new BatchedResponse.V1(new BatchedResponseV1(new QueryOutputBox.Vec(resultList), new ForwardCursor()));
                        queryFindAllAsset.getResultExtractor().extract(batch);
                        return session;
                    }
            )*/;
}
