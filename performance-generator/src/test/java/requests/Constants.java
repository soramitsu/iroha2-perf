package requests;

import io.gatling.javaapi.core.FeederBuilder;
import jp.co.soramitsu.iroha2.CryptoUtils;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.generated.DomainId;
import jp.co.soramitsu.iroha2.generated.Name;

import java.security.KeyPair;

import static io.gatling.javaapi.core.CoreDsl.csv;

public class Constants {

    public static final String URL_QUERY = "query";

    public static final String URL_STATUS = "status";

    public static final String URL_TRANSACTION = "api/transaction";

    public static final String DEFAULT_DOMAIN = "wonderland";

    public static final String ALICE_ACCOUNT = "alice";

    public static final String ALICE_ACCOUNT_RC20_ID = "alice@wonderland";

    public static final KeyPair ALICE_KEYPAIR = CryptoUtils.keyPairFromHex(
            "7233bfc89dcbd68c19fde6ce6158225298ec1131b6a130d1aeb454c1ab5183c0",
            "9ac47abf59b356e0bd7dcbbbb4dec080e302156a48ca907e47cb6aea1d32719e"
    );

    public static final DomainId WONDERLAND_DOMAIN_ID = ExtensionsKt.asDomainId(DEFAULT_DOMAIN);

    public static final DomainId NEW_DOMAIN_ID = new DomainId(new Name("new_domain_name"));

    public static final String SMART_CONTRACT_ACTIVATION_CODE = "019153fc-8d19-7faa-854a-57657ba0ed20";

    public static FeederBuilder<String> CSV_FEEDER = csv("iroha2_config/2_0_0-rc_1/f348b9a8/preconditionList.csv").circular();

    public static FeederBuilder<String> PEERS_FEEDER = csv("peers.csv").circular();

    public static FeederBuilder<String> MULTI_TXS_FEEDER = csv("iroha2_config/main/995da4ec/preconditionListMultiTxs.csv").circular();

    public static FeederBuilder<String> ACCOUNT_IDS_RC_20_TRIGGERS_TEST = csv("iroha2_config/stable/5d44d59/accountIds.csv").queue();

    public static FeederBuilder<String> DOMAIN_IDS_RC_20_TRIGGERS_TEST = csv("iroha2_config/stable/5d44d59/accountIds.csv").circular();

    public static FeederBuilder<String> ASSET_DEFINITION_IDS_RC_20_REGISTER_BOND_TRIGGERS_TEST = csv("iroha2_config/stable/5d44d59/assetDefinitionIds.csv").queue();

    public static FeederBuilder<String> ASSET_DEFINITION_IDS_RC_20_BUY_BOND_TRIGGERS_TEST = csv("iroha2_config/stable/5d44d59/assetDefinitionIds.csv").circular();

    public static FeederBuilder<String> NEW_BOND_IDS_RC_20_BUY_BOND_TRIGGERS_TEST_QUEUE = csv("iroha2_config/stable/5d44d59/bondIds.csv").queue();

    public static FeederBuilder<String> NEW_BOND_IDS_RC_20_BUY_BOND_TRIGGERS_TEST_CIRCULAR = csv("iroha2_config/stable/5d44d59/bondIds.csv").circular();
}
