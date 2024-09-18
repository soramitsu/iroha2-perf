package requests;

import io.gatling.javaapi.core.ChainBuilder;
import javassist.bytecode.ByteArray;
import jp.co.soramitsu.iroha2.CryptoUtils;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.generated.*;
import jp.co.soramitsu.iroha2.query.QueryAndExtractor;
import jp.co.soramitsu.iroha2.query.QueryBuilder;

import java.util.List;
import java.util.ArrayList;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static jp.co.soramitsu.iroha2.client.Iroha2Client.QUERY_ENDPOINT;

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

    /*
    отправить запрос гатлингом
        проверить есть ли батчи
        если батчи есть
            отправить дозапросы на эти батчи
    */

    /*private static QueryAndExtractor queryFindAllAsset;
    private static BatchedResponse responseDecoded;
    private static byte[] response;

    public static ChainBuilder paginatedQueryPostFindAllAssets = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(session -> {
                    ForwardCursor cursor = null;
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
                                         return SignedQuery.Companion.encode(queryFindAllAsset.getQuery());
                                    }
                            )
                    //сохранил тело ответа как байт массив
                    ).check(bodyBytes().saveAs("fullResponse")) // save id
            )
            .exec(session -> {
                //реализовать работу с результатом предыдущего запроса
                //ты знаешь какой будет курсор, а что если пропустить шаг с получением курсора и сразу имплементировать получение батчей
                        return session;
                    }
            )
            .exec(http("findAllAssets")
                            .post(session -> {
                                        return session.getString("peer") + URL_QUERY;
                                    }
                            )
                            .body(ByteArrayBody(session -> {
                                                ForwardCursor cursor = null;
                                                QueryAndExtractor queryFindAllAsset = QueryBuilder
                                                        .findAllAssets()
                                                        .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                                        .buildSigned(CryptoUtils.keyPairFromHex(
                                                                session.getString("publicKeySender"),
                                                                session.getString("privateKeySender")));



                                                var responseDecoded = sendQueryRequest(queryFindAllAsset, cursor);
                                                BatchedResponse.V1 decodedCursor = null;
                                                decodedCursor = (BatchedResponse.V1) responseDecoded.;
                                                if (decodedCursor.getBatchedResponseV1().getCursor() == null) {
                                                    var finalResult = queryFindAllAsset.getResultExtractor().extract((responseDecoded);
                                                    return finalResult;
                                                } else {
                                                    List<QueryOutputBox> resultList = getQueryResultWithCursor(queryFindAllAsset, decodedCursor.getBatchedResponseV1().getCursor());
                                                    resultList.add(responseDecoded.getBatch());
                                                    var finalResult = queryFindAllAsset.getResultExtractor().extract(new BatchedResponse.V1(
                                                            new BatchedResponseV1(new QueryOutputBox.Vec(resultList), new ForwardCursor())));
                                                    return finalResult;
                                                }
                                            }
                                    )
                            )
            );

    private static BatchedResponse<QueryOutputBox> sendQueryRequest(QueryAndExtractor queryAndExtractor, ForwardCursor cursor) {
        if (cursor == null) {
            return client.post(getApiUrl() + QUERY_ENDPOINT,
                    request -> request.setBody(SignedQuery.encode(queryAndExtractor.getQuery()))
            ).thenApply(response -> {
                byte[] responseBody = response;
                return BatchedResponse.Companion.decode(responseBody);
            });
        } else {
            return client.post(getApiUrl() + QUERY_ENDPOINT,
                    request -> {
                        request.parameter("query", cursor.getQuery());
                        request.parameter("cursor", cursor.getCursor() != null ? cursor.getCursor().getU64() : null);
                    }
            ).thenApply(response -> {
                byte[] responseBody = response.body();
                return BatchedResponseV1.Companion.decode(responseBody);
            });
        }
    }

    private static List<QueryOutputBox> getQueryResultWithCursor(QueryAndExtractor queryAndExtractor, ForwardCursor queryCursor) {
        if (queryCursor == null) {
            queryCursor = new ForwardCursor();
        }
        List<QueryOutputBox> resultList = new ArrayList<>();
        BatchedResponseV1<QueryOutputBox> responseDecoded = sendQueryRequest(queryAndExtractor, queryCursor);
        resultList.add(responseDecoded.getBatch());
        var cursor = responseDecoded.getCursor();
        if (cursor.getCursor() == null) {
            return resultList;
        } else {
            resultList.addAll(getQueryResultWithCursor(queryAndExtractor, cursor));
            return resultList;
        }
    }*/

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
