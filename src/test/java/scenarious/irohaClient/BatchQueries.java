package scenarious.irohaClient;

import io.gatling.javaapi.core.ScenarioBuilder;
import jp.co.soramitsu.iroha2.CryptoUtils;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.client.Iroha2AsyncClient;
import jp.co.soramitsu.iroha2.generated.*;
import jp.co.soramitsu.iroha2.model.IrohaUrls;
import jp.co.soramitsu.iroha2.query.QueryAndExtractor;
import jp.co.soramitsu.iroha2.query.QueryBuilder;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.scenario;


public class BatchQueries {
    private static Iroha2AsyncClient client;

    public static ScenarioBuilder findAllAsset = scenario("findAllAsset")
            .feed(csv("preconditionList.csv").circular()).feed(csv("peers.csv").circular())
            .exec(session -> {
                        try {
                            client = builderAsyncClient(session.getString("peer"));
                            System.out.println(System.currentTimeMillis());
                            QueryAndExtractor<List<Asset>> query = QueryBuilder
                                    .findAllAssets()
                                    .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                    .buildSigned(CryptoUtils.keyPairFromHex(session.getString("publicKeySender"), session.getString("privateKeySender")));

                            int i = client.sendQueryAsCompletableFuture(query).get().size();
                            return session;
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        } catch (ExecutionException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );

    private static Iroha2AsyncClient builderAsyncClient(String targetPeer) throws MalformedURLException {
        List<IrohaUrls> irohaUrls = new ArrayList<>();
        System.out.println(targetPeer);
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
