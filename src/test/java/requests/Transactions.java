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
                                                        /*TransactionBuilder.Companion.builder()
                                                                .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                                                .chainId(Constants.CHAIN_ID)
                                                                .transferAsset(ExtensionsKt.asAssetId(session.getString("anotherDevAssetIdSender")),
                                                                        1,
                                                                        ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdReceiver")))
                                                                .buildSigned(CryptoUtils.keyPairFromHex(
                                                                        session.getString("publicKeySender"),
                                                                        session.getString("privateKeySender")
                                                                ))*/
                                                        TransactionBuilder.Companion.builder()
                                                                .account(ExtensionsKt.asAccountId("b75167504b664ff4e5ab4016c3131a5578e71bc3e705cfa7776c04178103a59a@bulb_ffafd84a-6994-40f9-9f0e-a461da5d6482_e50583d2-72f7-44e1-8b80-d269bd0b081b"))
                                                                .chainId(Constants.CHAIN_ID)
                                                                .transferAsset(ExtensionsKt.asAssetId("xor4bad0d98-a969-471b-b733-57113b7c2541_6c3526c2-ea1d-48a1-9a34-27af3eb3dc2b##b75167504b664ff4e5ab4016c3131a5578e71bc3e705cfa7776c04178103a59a@bulb_ffafd84a-6994-40f9-9f0e-a461da5d6482_e50583d2-72f7-44e1-8b80-d269bd0b081b"),
                                                                        1,
                                                                        ExtensionsKt.asAccountId("6462b8b672c478dd3ba0097fcc405bc4396fdc459720cf63299467efb84997ec@bulb_ffafd84a-6994-40f9-9f0e-a461da5d6482_e50583d2-72f7-44e1-8b80-d269bd0b081b"))
                                                                .buildSigned(CryptoUtils.keyPairFromHex(
                                                                        session.getString("b75167504b664ff4e5ab4016c3131a5578e71bc3e705cfa7776c04178103a59a"),
                                                                        session.getString("ab78074d854c3cdef2b9aa83a2f6680e987414122d433dd440f4624228e7cc76")
                                                                ))
                                                );
                                            }
                                    )
                            )
            ).exec(http("tx_register_domain_status").get(Constants.URL_STATUS).check(status().is(200)));
}
