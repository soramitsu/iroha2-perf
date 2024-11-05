package smartcontractService;

import jp.co.soramitsu.exceptions.ClientErrorException;
import jp.co.soramitsu.iroha2.generated.SignedTransaction;
import objects.Bond;


public interface BondService {

    SignedTransaction getSignedRegisterBondAssetTx(Bond bond, String accountId) throws ClientErrorException;

}
