package smartcontractService;

import jp.co.soramitsu.iroha2.CryptoUtils;

import java.security.KeyPair;

public class Constants {

    public static final String URL_TRANSACTION = "api/transaction";

    public static final KeyPair ALICE_KEYPAIR = CryptoUtils.keyPairFromHex(
            "7233bfc89dcbd68c19fde6ce6158225298ec1131b6a130d1aeb454c1ab5183c0",
            "9ac47abf59b356e0bd7dcbbbb4dec080e302156a48ca907e47cb6aea1d32719e"
    );

    public static final String ALICE_ACCOUNT_RC20_ID = "alice@wonderland";
}
