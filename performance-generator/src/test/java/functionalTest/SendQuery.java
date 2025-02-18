package functionalTest;

import jp.co.soramitsu.iroha2.CryptoUtils;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.client.Iroha2AsyncClient;
import jp.co.soramitsu.iroha2.generated.Asset;
import jp.co.soramitsu.iroha2.model.IrohaUrls;
import jp.co.soramitsu.iroha2.query.QueryAndExtractor;
import jp.co.soramitsu.iroha2.query.QueryBuilder;
import kotlin.coroutines.Continuation;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SendQuery {

    private final String credentials = "";

    //@Test
   /* public void findAllAssets() {
        Iroha2AsyncClient client;
        try {
            client = builderAsyncClient("/peer-0");
            QueryAndExtractor<List<Asset>> query = QueryBuilder
                    .findAllAssets()
                    .account(ExtensionsKt.asAccountId("63f15f9c0ab4c0ff4461d829d27a569305f424ffb75cafaf9c4ab23e073fe2cc@bulb_44e22b24-5da5-4e19-ba2c-44164c8d7672_2a3db748-bd58-459c-9ebe-c5eb96e28d2b"))
                    .buildSigned(CryptoUtils.keyPairFromHex("63f15f9c0ab4c0ff4461d829d27a569305f424ffb75cafaf9c4ab23e073fe2cc", "14ca5693b524dae42805d9250aea4a92278ae961c900b5d815b1915078588306"));
            client.sendQueryAsync(query).get().size();
        } catch (MalformedURLException | InterruptedException | ExecutionException e) {
            System.out.println("EXEPTION: " + e.getMessage());
            throw new RuntimeException(e);
        }

    }

    private Iroha2AsyncClient builderAsyncClient(String targetPeer) throws MalformedURLException {
        List<IrohaUrls> irohaUrls = new ArrayList<>();

        irohaUrls.add(new IrohaUrls(new URL("https://iroha2.test.tachi.soramitsu.co.jp" + targetPeer),
                new URL("https://iroha2.test.tachi.soramitsu.co.jp"),
                new URL("https://iroha2.test.tachi.soramitsu.co.jp" + targetPeer)
            )
        );

        return new Iroha2AsyncClient(
                irohaUrls,
                true,
                credentials,
                1000);

    }*/
}
