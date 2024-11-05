package smartcontractService;

import jp.co.soramitsu.iroha2.generated.SignedTransaction;

public interface SmartContractService {

    SignedTransaction deployRegisterRegisterBondTrigger(String activationCode);

    SignedTransaction deployRegisterBuyBondsTrigger(String activationCode);

    SignedTransaction deployRegisterRedeemBondsTrigger(String activationCode);

}
