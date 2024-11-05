package requests;

import io.gatling.javaapi.core.ChainBuilder;
import jp.co.soramitsu.domain.transfer.enums.TransactionType;
import jp.co.soramitsu.iroha2.CryptoUtils;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.Permissions;
import jp.co.soramitsu.iroha2.generated.*;
import jp.co.soramitsu.iroha2.transaction.TransactionBuilder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class Transactions extends Constants {

    public static ChainBuilder postRegisterDefinitionId = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(
                    http("tx_register_definition_id")
                            .post(session -> {
                                        return session.getString("peer") + URL_TRANSACTION;
                                    }
                            )
                            .body(ByteArrayBody(session -> {
                                                return SignedTransaction.Companion.encode(
                                                        TransactionBuilder.Companion.builder()
                                                                .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                                                .registerAssetDefinition(
                                                                        ExtensionsKt.asAssetDefinitionId("performance_token_#" + session.getString("domainIdSender")),
                                                                        new AssetValueType.Quantity()
                                                                )
                                                                .buildSigned(ALICE_KEYPAIR)
                                                );
                                            }
                                    )
                            )
            ).exec(http("tx_register_definition_id_status").get(URL_STATUS).check(status().is(200)));

    public static ChainBuilder postMintAsset =
            exec(feed(CSV_FEEDER))
                    .exec(feed(ACCOUNT_IDS_RC_20_TRIGGERS_TEST))
                    .exec(http("mint asset")
                            .post(session -> {
                                        return session.getString("peer") + URL_TRANSACTION;
                                    }
                            )
                            .body(ByteArrayBody(session -> {
                                                return SignedTransaction.Companion.encode(
                                                        TransactionBuilder.Companion.builder()
                                                                .account(ExtensionsKt.asAccountId(session.getString("accountIdForTrigger")))
                                                                .mintAsset(new AssetId(
                                                                                ExtensionsKt.asAssetDefinitionId(session.getString("")),
                                                                                ExtensionsKt.asAccountId(session.getString("accountIdForTrigger"))
                                                                        ),
                                                                        new BigDecimal(10_000_000))
                                                                .buildSigned(ALICE_KEYPAIR)
                                                );
                                            }
                                    )
                            )
                    );

    //TODO: It must keep running in a loop
    // EXECUTION DELAY after called the scn 10,5 sec
    public static ChainBuilder grantPermissionForTriggers =
            exec(feed(CSV_FEEDER))
                    .exec(feed(PEERS_FEEDER))
                    .exec(http("tx_grand_permission")
                            .post(session -> {
                                        return session.getString("peer") + URL_TRANSACTION;
                                    }
                            )
                            .body(ByteArrayBody(session -> {
                                                final var grantPermissionsForSmartContractsTrx = TransactionBuilder.Companion.builder()
                                                        .grantPermissionToken(
                                                                Permissions.CanRemoveKeyValueInUserAccount,
                                                                ExtensionsKt.asJsonString(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender"))),
                                                                ExtensionsKt.asAccountId(ALICE_ACCOUNT_RC20_ID))
                                                        .grantPermissionToken(
                                                                Permissions.CanSetKeyValueInUserAccount,
                                                                ExtensionsKt.asJsonString(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender"))),
                                                                ExtensionsKt.asAccountId(ALICE_ACCOUNT_RC20_ID))
                                                        .grantPermissionToken(
                                                                Permissions.CanTransferUserAssetsToken,
                                                                ExtensionsKt.asJsonString(ExtensionsKt.asAssetId(session.getString("anotherDevAssetIdSender"))),
                                                                ExtensionsKt.asAccountId(ALICE_ACCOUNT_RC20_ID))
                                                        .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                                        .buildSigned(ALICE_KEYPAIR);

                                                return SignedTransaction.Companion.encode(grantPermissionsForSmartContractsTrx);
                                            }
                                    )
                            )
                    );

    public static ChainBuilder buySomeBondsBondAssetTrigger =
            exec(feed(CSV_FEEDER))
                    .exec(feed(PEERS_FEEDER))
                    .exec(feed(NEW_BOND_IDS_RC_20_BUY_BOND_TRIGGERS_TEST_CIRCULAR))
                    .exec(http("tx_buy_bonds")
                            .post(session -> {
                                        return session.getString("peer") + URL_TRANSACTION;
                                    }
                            )
                            .body(ByteArrayBody(session -> {
                                                final var map = new HashMap<Name, Value>();
                                                map.put(ExtensionsKt.asName("bond"),
                                                        ExtensionsKt.asValue(ExtensionsKt.asAssetDefinitionId(session.getString("bondId"))));

                                                map.put(ExtensionsKt.asName("quantity"),
                                                        ExtensionsKt.asValue(2));

                                                final var metadata = new Metadata(map);
                                                final var limitedMetadata = new Value.LimitedMetadata(metadata);

                                                final var buyBonds = TransactionBuilder.Companion.builder()
                                                        .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                                        .setKeyValue(
                                                                ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")),
                                                                ExtensionsKt.asName("buy_bonds"),
                                                                limitedMetadata);

                                                buyBonds.getMetadata().getValue().put(
                                                        ExtensionsKt.asName("transaction_type"),
                                                        ExtensionsKt.asValue(TransactionType.BOND_ASSET_PURCHASE.name()));

                                                return SignedTransaction.Companion.encode(buyBonds.buildSigned(ALICE_KEYPAIR));
                                            }
                                    )
                            )
                    );

    public static ChainBuilder triggeringBondAssetSmartContract =
            exec(feed(CSV_FEEDER))
                    .exec(feed(PEERS_FEEDER))
                    .exec(http("tx_triggering_bond_asset_smart_contract")
                            .post(session -> {
                                        return session.getString("peer") + URL_TRANSACTION;
                                    }
                            )
                            .body(ByteArrayBody(session -> {
                                                final var dummyTransaction = TransactionBuilder.Companion.builder()
                                                        .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                                        .setKeyValue(
                                                                ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")),
                                                                ExtensionsKt.asName("dummy"), ExtensionsKt.asValue(1))
                                                        .buildSigned(ALICE_KEYPAIR);
                                                return SignedTransaction.Companion.encode(dummyTransaction);
                                            }
                                    )
                            )
                    );
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes

    /*
        for CBDC
        SCN 1. кидаю 1 транзакцию на изменение метадаты по всем зарегистрированным пользователям
        SCN 2. кидаю несколько транзакций на изменение метадаты на 10 условных пользователей
    */

    //уточнить сколько раз должна пролетать транзакция  должна ли она пролетать для пользователя или для ассет дефинишена или для триггера
    public static ChainBuilder redeemBondsBondAssetTrigger =
            exec(feed(CSV_FEEDER))
                    .exec(feed(PEERS_FEEDER))
                    .exec(feed(ASSET_DEFINITION_IDS_RC_20_BUY_BOND_TRIGGERS_TEST))
                    .exec(http("tx_redeem_bond")
                            .post(session -> {
                                        return session.getString("peer") + URL_TRANSACTION;
                                    }
                            )
                            .body(ByteArrayBody(session -> {
                                                final var map = new HashMap<Name, Value>();
                                                map.put(ExtensionsKt.asName("bond"),
                                                        ExtensionsKt.asValue(ExtensionsKt.asAssetDefinitionId(session.getString("assetDefinitionIdForTrigger"))));

                                                map.put(ExtensionsKt.asName("quantity"),
                                                        ExtensionsKt.asValue(2));

                                                final var metadata = new Metadata(map);
                                                final var limitedMetadata = new Value.LimitedMetadata(metadata);

                                                final var redeemBonds = TransactionBuilder.Companion.builder()
                                                        .account(ExtensionsKt.asAccountId(ALICE_ACCOUNT_RC20_ID))
                                                        .setKeyValue(
                                                                ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")),
                                                                ExtensionsKt.asName("redeem_bonds"),
                                                                limitedMetadata);

                                                redeemBonds.getMetadata().getValue().put(
                                                        ExtensionsKt.asName("transaction_type"),
                                                        ExtensionsKt.asValue(TransactionType.BOND_REDEMPTION_PAYMENT.name()));

                                                return SignedTransaction.Companion.encode(redeemBonds.buildSigned(ALICE_KEYPAIR));
                                            }
                                    )
                            )
                    );

    public static ChainBuilder postMultiInstructions = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER)).exec(feed(MULTI_TXS_FEEDER))
            .exec(
                    http("tx_multi_instruction")
                            .post(session -> {
                                        return session.getString("peer") + URL_TRANSACTION;
                                    }
                            )
                            .body(ByteArrayBody(session -> {
                                                return SignedTransaction.Companion.encode(
                                                        TransactionBuilder.Companion.builder()
                                                                .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                                                //.chainId(Constants.CHAIN_ID)
                                                                .registerDomain(ExtensionsKt.asDomainId(session.getString("domainId_0")))
                                                                .registerDomain(ExtensionsKt.asDomainId(session.getString("domainId_1")))
                                                                .registerDomain(ExtensionsKt.asDomainId(session.getString("domainId_2")))

                                                                /*.registerAssetDefinition(ExtensionsKt.asAssetDefinitionId(session.getString("assetDefinitionId_0")), new AssetValueType.Quantity())
                                                                .registerAssetDefinition(ExtensionsKt.asAssetDefinitionId(session.getString("assetDefinitionId_1")), new AssetValueType.Quantity())
                                                                .registerAssetDefinition(ExtensionsKt.asAssetDefinitionId(session.getString("assetDefinitionId_2_0")), new AssetValueType.Quantity())
                                                                .registerAssetDefinition(ExtensionsKt.asAssetDefinitionId(session.getString("assetDefinitionId_2_1")), new AssetValueType.Quantity())*/

                                                                /*.registerAccount(ExtensionsKt.asAccountId(session.getString("accountIdReceiver_0")))
                                                                .registerAccount(ExtensionsKt.asAccountId(session.getString("accountIdReceiver_1")))
                                                                .registerAccount(ExtensionsKt.asAccountId(session.getString("accountIdReceiver_2")))
                                                                .registerAccount(ExtensionsKt.asAccountId(session.getString("accountIdReceiver_3")))
                                                                .registerAccount(ExtensionsKt.asAccountId(session.getString("accountIdReceiver_4")))
                                                                .registerAccount(ExtensionsKt.asAccountId(session.getString("accountIdReceiver_5")))
                                                                .registerAccount(ExtensionsKt.asAccountId(session.getString("accountIdReceiver_6")))
                                                                .registerAccount(ExtensionsKt.asAccountId(session.getString("accountIdReceiver_7")))
                                                                .registerAccount(ExtensionsKt.asAccountId(session.getString("accountIdReceiver_8")))*/
                                                                .registerAccount(ExtensionsKt.asAccountId(session.getString("accountIdSender_0")), Collections.singletonList(ExtensionsKt.toIrohaPublicKey(ALICE_KEYPAIR.getPublic())))
                                                                .registerAccount(ExtensionsKt.asAccountId(session.getString("accountIdSender_1")), Collections.singletonList(ExtensionsKt.toIrohaPublicKey(ALICE_KEYPAIR.getPublic())))
                                                                .registerAccount(ExtensionsKt.asAccountId(session.getString("accountIdSender_2")), Collections.singletonList(ExtensionsKt.toIrohaPublicKey(ALICE_KEYPAIR.getPublic())))
                                                                .registerAccount(ExtensionsKt.asAccountId(session.getString("accountIdSender_3")), Collections.singletonList(ExtensionsKt.toIrohaPublicKey(ALICE_KEYPAIR.getPublic())))
                                                                .registerAccount(ExtensionsKt.asAccountId(session.getString("accountIdSender_4")), Collections.singletonList(ExtensionsKt.toIrohaPublicKey(ALICE_KEYPAIR.getPublic())))
                                                                .registerAccount(ExtensionsKt.asAccountId(session.getString("accountIdSender_5")), Collections.singletonList(ExtensionsKt.toIrohaPublicKey(ALICE_KEYPAIR.getPublic())))
                                                                .registerAccount(ExtensionsKt.asAccountId(session.getString("accountIdSender_6")), Collections.singletonList(ExtensionsKt.toIrohaPublicKey(ALICE_KEYPAIR.getPublic())))
                                                                .registerAccount(ExtensionsKt.asAccountId(session.getString("accountIdSender_7")), Collections.singletonList(ExtensionsKt.toIrohaPublicKey(ALICE_KEYPAIR.getPublic())))
                                                                .registerAccount(ExtensionsKt.asAccountId(session.getString("accountIdSender_8")), Collections.singletonList(ExtensionsKt.toIrohaPublicKey(ALICE_KEYPAIR.getPublic())))

                                                                /*.registerAsset(ExtensionsKt.asAssetId(session.getString("assetId_0")), new AssetValue.Numeric(new Numeric(BigInteger.TEN, Long.parseLong("1"))))
                                                                .registerAsset(ExtensionsKt.asAssetId(session.getString("assetId_1")), new AssetValue.Numeric(new Numeric(BigInteger.TEN, Long.parseLong("1"))))
                                                                .registerAsset(ExtensionsKt.asAssetId(session.getString("assetId_2")), new AssetValue.Numeric(new Numeric(BigInteger.TEN, Long.parseLong("1"))))
                                                                .registerAsset(ExtensionsKt.asAssetId(session.getString("assetId_3")), new AssetValue.Numeric(new Numeric(BigInteger.TEN, Long.parseLong("1"))))
                                                                .registerAsset(ExtensionsKt.asAssetId(session.getString("assetId_4")), new AssetValue.Numeric(new Numeric(BigInteger.TEN, Long.parseLong("1"))))
                                                                .registerAsset(ExtensionsKt.asAssetId(session.getString("assetId_5")), new AssetValue.Numeric(new Numeric(BigInteger.TEN, Long.parseLong("1"))))
                                                                .registerAsset(ExtensionsKt.asAssetId(session.getString("assetId_6")), new AssetValue.Numeric(new Numeric(BigInteger.TEN, Long.parseLong("1"))))
                                                                .registerAsset(ExtensionsKt.asAssetId(session.getString("assetId_7")), new AssetValue.Numeric(new Numeric(BigInteger.TEN, Long.parseLong("1"))))
                                                                .registerAsset(ExtensionsKt.asAssetId(session.getString("assetId_8")), new AssetValue.Numeric(new Numeric(BigInteger.TEN, Long.parseLong("1"))))
                                                                .registerAsset(ExtensionsKt.asAssetId(session.getString("assetId_0")), new AssetValue.Quantity(1))
                                                                .registerAsset(ExtensionsKt.asAssetId(session.getString("assetId_1")), new AssetValue.Quantity(1))
                                                                .registerAsset(ExtensionsKt.asAssetId(session.getString("assetId_2")), new AssetValue.Quantity(1))
                                                                .registerAsset(ExtensionsKt.asAssetId(session.getString("assetId_3")), new AssetValue.Quantity(1))
                                                                .registerAsset(ExtensionsKt.asAssetId(session.getString("assetId_4")), new AssetValue.Quantity(1))
                                                                .registerAsset(ExtensionsKt.asAssetId(session.getString("assetId_5")), new AssetValue.Quantity(1))
                                                                .registerAsset(ExtensionsKt.asAssetId(session.getString("assetId_6")), new AssetValue.Quantity(1))
                                                                .registerAsset(ExtensionsKt.asAssetId(session.getString("assetId_7")), new AssetValue.Quantity(1))
                                                                .registerAsset(ExtensionsKt.asAssetId(session.getString("assetId_8")), new AssetValue.Quantity(1))*/

                                                                .mintAsset(ExtensionsKt.asAssetId(session.getString("assetId_0")), 100)
                                                                .mintAsset(ExtensionsKt.asAssetId(session.getString("assetId_1")), 100)
                                                                .mintAsset(ExtensionsKt.asAssetId(session.getString("assetId_2")), 100)
                                                                .mintAsset(ExtensionsKt.asAssetId(session.getString("assetId_3")), 100)
                                                                .mintAsset(ExtensionsKt.asAssetId(session.getString("assetId_4")), 100)
                                                                .mintAsset(ExtensionsKt.asAssetId(session.getString("assetId_5")), 100)
                                                                .mintAsset(ExtensionsKt.asAssetId(session.getString("assetId_6")), 100)
                                                                .mintAsset(ExtensionsKt.asAssetId(session.getString("assetId_7")), 100)
                                                                .mintAsset(ExtensionsKt.asAssetId(session.getString("assetId_8")), 100)

                                                                .transferAsset(ExtensionsKt.asAssetId(session.getString("assetId_0")), 1, ExtensionsKt.asAccountId(session.getString("accountIdReceiver_0")))
                                                                .burnAsset(ExtensionsKt.asAssetId(session.getString("assetId_0")), 1)

                                                                .transferAsset(ExtensionsKt.asAssetId(session.getString("assetId_1")), 1, ExtensionsKt.asAccountId(session.getString("accountIdReceiver_1")))
                                                                .burnAsset(ExtensionsKt.asAssetId(session.getString("assetId_1")), 1)

                                                                .transferAsset(ExtensionsKt.asAssetId(session.getString("assetId_2")), 1, ExtensionsKt.asAccountId(session.getString("accountIdReceiver_2")))
                                                                .burnAsset(ExtensionsKt.asAssetId(session.getString("assetId_2")), 1)

                                                                .transferAsset(ExtensionsKt.asAssetId(session.getString("assetId_3")), 1, ExtensionsKt.asAccountId(session.getString("accountIdReceiver_3")))
                                                                .burnAsset(ExtensionsKt.asAssetId(session.getString("assetId_3")), 1)

                                                                .transferAsset(ExtensionsKt.asAssetId(session.getString("assetId_4")), 1, ExtensionsKt.asAccountId(session.getString("accountIdReceiver_4")))
                                                                .burnAsset(ExtensionsKt.asAssetId(session.getString("assetId_4")), 1)

                                                                .transferAsset(ExtensionsKt.asAssetId(session.getString("assetId_5")), 1, ExtensionsKt.asAccountId(session.getString("accountIdReceiver_5")))
                                                                .burnAsset(ExtensionsKt.asAssetId(session.getString("assetId_5")), 1)

                                                                .transferAsset(ExtensionsKt.asAssetId(session.getString("assetId_6")), 1, ExtensionsKt.asAccountId(session.getString("accountIdReceiver_6")))
                                                                .burnAsset(ExtensionsKt.asAssetId(session.getString("assetId_6")), 1)

                                                                .transferAsset(ExtensionsKt.asAssetId(session.getString("assetId_7")), 1, ExtensionsKt.asAccountId(session.getString("accountIdReceiver_7")))
                                                                .burnAsset(ExtensionsKt.asAssetId(session.getString("assetId_7")), 1)

                                                                .transferAsset(ExtensionsKt.asAssetId(session.getString("assetId_8")), 1, ExtensionsKt.asAccountId(session.getString("accountIdReceiver_8")))
                                                                .burnAsset(ExtensionsKt.asAssetId(session.getString("assetId_8")), 1)

                                                                .buildSigned(ALICE_KEYPAIR));
                                            }
                                    )
                            )
            ).exec(http("tx_multi_instruction_status").get(URL_STATUS).check(status().is(200)));

    public static ChainBuilder postRevertWorldViewMultiInstructions = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER)).exec(feed(MULTI_TXS_FEEDER))
            .exec(
                    http("tx_unregister_domain")
                            .post(session -> {
                                        return session.getString("peer") + URL_TRANSACTION;
                                    }
                            )
                            .body(ByteArrayBody(session -> {
                                                return SignedTransaction.Companion.encode(
                                                        TransactionBuilder.Companion.builder()
                                                                .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                                                .unregisterDomain(ExtensionsKt.asDomainId(session.getString("domainId_0")))
                                                                .unregisterDomain(ExtensionsKt.asDomainId(session.getString("domainId_1")))
                                                                .unregisterDomain(ExtensionsKt.asDomainId(session.getString("domainId_2")))

                                                                .buildSigned(ALICE_KEYPAIR)
                                                );
                                            }
                                    )
                            )
            ).exec(http("tx_unregister_domain_status").get(URL_STATUS).check(status().is(200)));

    //TODO: special genesis with 500000 definitionId include 10 assets, object 50000
    public static ChainBuilder postUnregisterDefinitionId = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(
                    http("tx_register_definition_id")
                            .post(session -> {
                                        return session.getString("peer") + URL_TRANSACTION;
                                    }
                            )
                            .body(ByteArrayBody(session -> {
                                                return SignedTransaction.Companion.encode(
                                                        TransactionBuilder.Companion.builder()
                                                                .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                                                //.unregisterAssetDefinition(ExtensionsKt.asAssetDefinitionId("performance_token_#" + session.getString("domainIdSender")))
                                                                .buildSigned(ALICE_KEYPAIR)
                                                );
                                            }
                                    )
                            )
            ).exec(http("tx_register_definition_id_status").get(URL_STATUS).check(status().is(200)));

    public static ChainBuilder postRegisterDomain = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(
                    http("tx_register_domain")
                            .post(session -> {
                                        return session.getString("peer") + URL_TRANSACTION;
                                    }
                            )
                            .body(ByteArrayBody(session -> {
                                                return SignedTransaction.Companion.encode(
                                                        TransactionBuilder.Companion.builder()
                                                                .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                                                .registerDomain(NEW_DOMAIN_ID)
                                                                .buildSigned(ALICE_KEYPAIR)
                                                );
                                            }
                                    )
                            )
            ).exec(http("tx_register_domain_status").get(URL_STATUS).check(status().is(200)));

    public static ChainBuilder postTransferAsset = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(
                    http("tx_transfer_asset")
                            .post(session -> {
                                        return session.getString("peer") + URL_TRANSACTION;
                                    }
                            ).body(ByteArrayBody(session -> {
                                                return SignedTransaction.Companion.encode(
                                                        TransactionBuilder.Companion.builder()
                                                                .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")))
                                                                .transferAsset(ExtensionsKt.asAssetId(session.getString("anotherDevAssetIdSender")),
                                                                        1,
                                                                        ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdReceiver")))
                                                                .buildSigned(ALICE_KEYPAIR)
                                                );
                                            }
                                    )
                            )
            ).exec(http("tx_transfer_asset_status").get(session -> {
                return session.getString("peer") + URL_STATUS;
            }).check(status().is(200)));

    public static ChainBuilder debuggingPostTransferAsset = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(
                    http("debugging_tx_transfer_asset")
                            .post(session -> {
                                        return session.getString("peer") + URL_TRANSACTION;
                                    }
                            ).body(ByteArrayBody(session -> {
                                                return SignedTransaction.Companion.encode(
                                                        TransactionBuilder.Companion.builder()
                                                                .account(ExtensionsKt.asAccountId("b24ff6ab1e5923f125d7f4eb0c62528eb6b8e84139e17fb377e2bd5fc1498b19@bulb_7405035c-e673-4a46-8f7f-1cca0a0687fa_6138d709-b7e0-44e6-b5dd-1f26f848f464"))
                                                                .transferAsset(ExtensionsKt.asAssetId("xora08da796-bca3-475f-bf94-30cc918ef69b_c687b817-3a68-4848-81bc-d4915801cee1##b24ff6ab1e5923f125d7f4eb0c62528eb6b8e84139e17fb377e2bd5fc1498b19@bulb_7405035c-e673-4a46-8f7f-1cca0a0687fa_6138d709-b7e0-44e6-b5dd-1f26f848f464"),
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
            ).exec(http("debugging_tx_register_domain_status").get(URL_STATUS).check(status().is(200)))
            .exec(session -> {
                String responseBody = session.getString("responseBody");
                System.out.println("Response: " + responseBody);
                return session;
            });
}
