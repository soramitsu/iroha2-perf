package requests;

import io.gatling.javaapi.core.ChainBuilder;
import jp.co.soramitsu.iroha2.CryptoUtils;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.generated.*;
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
                                                                .unregisterAssetDefinition(ExtensionsKt.asAssetDefinitionId("performance_token_#" + session.getString("domainIdSender")))
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
            ).exec(http("tx_transfer_asset_status").get(Constants.URL_STATUS).check(status().is(200)));

    public static ChainBuilder debuggingPostTransferAsset = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(
                    http("debugging_tx_transfer_asset")
                            .post(session -> {
                                        return session.getString("peer") + Constants.URL_TRANSACTION;
                                    }
                            ).body(ByteArrayBody(session -> {
                                                return SignedTransaction.Companion.encode(
                                                        TransactionBuilder.Companion.builder()
                                                                /*crypto*/.account(ExtensionsKt.asAccountId("b24ff6ab1e5923f125d7f4eb0c62528eb6b8e84139e17fb377e2bd5fc1498b19@bulb_7405035c-e673-4a46-8f7f-1cca0a0687fa_6138d709-b7e0-44e6-b5dd-1f26f848f464"))
                                                                .chainId(Constants.CHAIN_ID)
                                                                /* drop ed0120*/.transferAsset(ExtensionsKt.asAssetId("xora08da796-bca3-475f-bf94-30cc918ef69b_c687b817-3a68-4848-81bc-d4915801cee1##b24ff6ab1e5923f125d7f4eb0c62528eb6b8e84139e17fb377e2bd5fc1498b19@bulb_7405035c-e673-4a46-8f7f-1cca0a0687fa_6138d709-b7e0-44e6-b5dd-1f26f848f464"),
                                                                        1,
                                                                        /*drop ed0120*/ExtensionsKt.asAccountId("7d5ad42de2c9c7f4b1faa7e12a9fc58d831189a59ad60daa6a3a88af9dc2a2e2@bulb_7405035c-e673-4a46-8f7f-1cca0a0687fa_6138d709-b7e0-44e6-b5dd-1f26f848f464"))
                                                                .buildSigned(CryptoUtils.keyPairFromHex(
                                                                        "b24ff6ab1e5923f125d7f4eb0c62528eb6b8e84139e17fb377e2bd5fc1498b19",
                                                                        "272d70e7efbee4f4c9bc61a2b30deb43194ac1b5e18d06fbba0374d3505c36c8"))
                                            );
                                            }
                                    )
                            ).check(
                                    status().is(200),
                                    bodyString().saveAs("responseBody")
                            )
            ).exec(http("debugging_tx_register_domain_status").get(Constants.URL_STATUS).check(status().is(200)))
            .exec(session -> {
                String responseBody = session.getString("responseBody");
                System.out.println("Response: " + responseBody);
                return session;
            });
}


