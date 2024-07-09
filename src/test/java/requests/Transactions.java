package requests;

import io.gatling.javaapi.core.ChainBuilder;
import jp.co.soramitsu.iroha2.CryptoUtils;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.generated.AssetType;
import jp.co.soramitsu.iroha2.generated.NumericSpec;
import jp.co.soramitsu.iroha2.generated.SignedTransaction;
import jp.co.soramitsu.iroha2.transaction.TransactionBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;


public class Transactions extends Constants {

    public static ChainBuilder postRegisterDefinitionId = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(
                    http("tx_register_definition_id")
                            .post(session -> {
                                        return session.getString("peer") + Constants.URL_TRANSACTION;
                                    }
                            )
                            .body(ByteArrayBody(session -> {
                                                return SignedTransaction.Companion.encode(
                                                        TransactionBuilder.Companion.builder()
                                                                .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                                                .chainId(Constants.CHAIN_ID)
                                                                .registerAssetDefinition(
                                                                        ExtensionsKt.asAssetDefinitionId("performance_token_#" + session.getString("domainIdSender")),
                                                                        new AssetType.Numeric(new NumericSpec())
                                                                )
                                                                .buildSigned(CryptoUtils.keyPairFromHex(
                                                                        session.getString("publicKeySender"),
                                                                        session.getString("privateKeySender")))
                                                );
                                            }
                                    )
                            )
            ).exec(http("tx_register_definition_id_status").get(Constants.URL_STATUS).check(status().is(200)));

    //TODO: special genesis with 500000 definitionId include 10 assets, object 50000
    public static ChainBuilder postUnregisterDefinitionId = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(
                    http("tx_register_definition_id")
                            .post(session -> {
                                        return session.getString("peer") + Constants.URL_TRANSACTION;
                                    }
                            )
                            .body(ByteArrayBody(session -> {
                                                return SignedTransaction.Companion.encode(
                                                        TransactionBuilder.Companion.builder()
                                                                .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                                                .chainId(Constants.CHAIN_ID)
                                                                .unregisterAssetDefinition(ExtensionsKt.asAssetDefinitionId("performance_token_#" + session.getString("domainIdSender"))                                                                )
                                                                .buildSigned(CryptoUtils.keyPairFromHex(
                                                                        session.getString("publicKeySender"),
                                                                        session.getString("privateKeySender")))
                                                );
                                            }
                                    )
                            )
            ).exec(http("tx_register_definition_id_status").get(Constants.URL_STATUS).check(status().is(200)));

    public static ChainBuilder postRegisterDomain = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(
                    http("tx_register_domain")
                            .post(session -> {
                                        return session.getString("peer") + Constants.URL_TRANSACTION;
                                    }
                            )
                            .body(ByteArrayBody(session -> {
                                                return SignedTransaction.Companion.encode(
                                                        TransactionBuilder.Companion.builder()
                                                                .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                                                .chainId(Constants.CHAIN_ID)
                                                                .registerDomain(Constants.NEW_DOMAIN_ID)
                                                                .buildSigned(CryptoUtils.keyPairFromHex(
                                                                        session.getString("publicKeySender"),
                                                                        session.getString("privateKeySender")))
                                                );
                                            }
                                    )
                            )
            ).exec(http("tx_register_domain_status").get(Constants.URL_STATUS).check(status().is(200)));

    public static ChainBuilder postTransferAsset = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(
                    http("tx_transfer_asset")
                            .post(session -> {
                                        return session.getString("peer") + Constants.URL_TRANSACTION;
                                    }
                            ).body(ByteArrayBody(session -> {
                                                return SignedTransaction.Companion.encode(
                                                        TransactionBuilder.Companion.builder()
                                                                .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                                                .chainId(Constants.CHAIN_ID)
                                                                .transferAsset(ExtensionsKt.asAssetId(session.getString("anotherDevAssetIdSender")),
                                                                        1,
                                                                        ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdReceiver")))
                                                                .buildSigned(CryptoUtils.keyPairFromHex(
                                                                        session.getString("publicKeySender"),
                                                                        session.getString("privateKeySender")
                                                                ))
                                                );
                                            }
                                    )
                            )
            ).exec(http("tx_register_domain_status").get(Constants.URL_STATUS).check(status().is(200)));
}
