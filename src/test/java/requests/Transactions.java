package requests;

import io.gatling.javaapi.core.ChainBuilder;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.generated.SignedTransaction;
import jp.co.soramitsu.iroha2.transaction.TransactionBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;


public class Transactions extends Constants {

    public static ChainBuilder txPostRegisterDomain = exec(feed(CSV_FEEDER))
            .exec(
                    http("registerDomain transaction")
                            .post(Constants.URL_TRANSACTION)
                            .body(ByteArrayBody(session -> {
                                                return SignedTransaction.Companion.encode(
                                                        TransactionBuilder.Companion.builder()
                                                                .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                                                .chainId(Constants.CHAIN_ID)
                                                                .registerDomain(Constants.NEW_DOMAIN_ID)
                                                                .buildSigned(Constants.ALICE_KEYPAIR));
                                            }
                                    )
                            )
            ).exec(http("registerDomain status").get(Constants.URL_STATUS).check(status().is(200)));
}
