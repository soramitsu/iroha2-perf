package scenarious.irohaClient;

import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Session;
import jp.co.soramitsu.iroha2.CryptoUtils;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.client.Iroha2AsyncClient;
import jp.co.soramitsu.iroha2.generated.*;
import jp.co.soramitsu.iroha2.model.IrohaUrls;
import jp.co.soramitsu.iroha2.query.QueryAndExtractor;
import jp.co.soramitsu.iroha2.query.QueryBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.scenario;


public class BatchQueries {
    private static Iroha2AsyncClient client;

    public static ScenarioBuilder findAllAssetClientMode = scenario("findAllAssetClientMode")
            .feed(csv("preconditionList.csv").circular()).feed(csv("peers.csv").circular())
            .exec(session -> {
                        try {
                            client = builderAsyncClient(session.getString("peer"));
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                        return session;
                    }
            ).repeat(10).on(
                    CoreDsl.exec(session -> {
                                try {
                                    client.sendQueryAsCompletableFuture(buildFindAllAssetsQuery(session)).get();
                                    return session;
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                    e.getMessage();
                                    throw new RuntimeException(e);
                                }
                            }
                    )
            );

    public static ScenarioBuilder findAssetByIdClientMode = scenario("findAssetByIdClientMode")
            .feed(csv("preconditionList.csv").circular()).feed(csv("peers.csv").circular())
            .exec(session -> {
                        try {
                            client = builderAsyncClient(session.getString("peer"));
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                        return session;
                    }
            ).repeat(10).on(
                    CoreDsl.exec(session -> {
                                try {
                                    client.sendQueryAsCompletableFuture(buildFindAssetByIdQuery(session)).get();
                                    return session;
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                    e.getMessage();
                                    throw new RuntimeException(e);
                                }
                            }
                    )
            );

    private static QueryAndExtractor<Asset> buildFindAssetByIdQuery (Session session) {
        return QueryBuilder
                .findAssetById(ExtensionsKt.asAssetId(session.getString("anotherDevAssetIdSender")))
                .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                .buildSigned(CryptoUtils.keyPairFromHex(
                        session.getString("publicKeySender"),
                        session.getString("privateKeySender")
                ));
    }

    private static QueryAndExtractor<List<Asset>> buildFindAllAssetsQuery(Session session) {
        return QueryBuilder
                .findAllAssets()
                .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                .buildSigned(CryptoUtils.keyPairFromHex(session.getString("publicKeySender"), session.getString("privateKeySender")));
    }

    private static Iroha2AsyncClient builderAsyncClient(String targetPeer) throws MalformedURLException {
        List<IrohaUrls> irohaUrls = new ArrayList<>();
        irohaUrls.add(new IrohaUrls(
                        new URL("https://iroha2.test.tachi.soramitsu.co.jp" + targetPeer),
                        new URL("https://iroha2.test.tachi.soramitsu.co.jp"),
                        new URL("https://iroha2.test.tachi.soramitsu.co.jp" + targetPeer)
                )
        );
        Iroha2AsyncClient client1 = new Iroha2AsyncClient(
                irohaUrls,
                true,
                System.getProperty("remoteLogin") + ":" + System.getProperty("remotePassword"),
                1000);

        return client1;
    }


}
