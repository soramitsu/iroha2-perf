package objects;

import jp.co.soramitsu.exceptions.ClientErrorException;
import io.gatling.javaapi.core.Session;
import jp.co.soramitsu.iroha2.generated.SignedTransaction;


public interface BondService {

    SignedTransaction getSignedRegisterBondAssetTx(CreateBond createBond, Session session) throws ClientErrorException;

}
