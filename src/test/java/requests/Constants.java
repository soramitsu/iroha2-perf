package requests;

import io.gatling.javaapi.core.FeederBuilder;
import jp.co.soramitsu.iroha2.CryptoUtils;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.generated.AccountId;
import jp.co.soramitsu.iroha2.generated.DomainId;
import jp.co.soramitsu.iroha2.generated.Name;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static io.gatling.javaapi.core.CoreDsl.csv;

public class Constants {
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
    public static final DomainId NEW_DOMAIN_ID = new DomainId(new Name("new_domain_name"));
    public static final UUID CHAIN_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public static FeederBuilder<String> CSV_FEEDER = csv("preconditionList.csv").circular();
    public static FeederBuilder<String> PEERS_FEEDER = csv("peers.csv").circular();
}
