package healper;

import jp.co.soramitsu.exceptions.ClientErrorException;
import io.gatling.javaapi.core.Session;
import jp.co.soramitsu.iroha2.generated.SignedTransaction;
import objects.Bond;


public interface BondService {

    SignedTransaction getSignedRegisterBondAssetTx(Bond bond, Session session) throws ClientErrorException;

}
