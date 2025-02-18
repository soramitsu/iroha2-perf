package requests;

import io.gatling.javaapi.core.ChainBuilder;
import jp.co.soramitsu.domain.transfer.enums.TransactionType;
import jp.co.soramitsu.iroha2.CryptoUtils;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.ModelPermission;
import jp.co.soramitsu.iroha2.Permissions;
import jp.co.soramitsu.iroha2.generated.*;
import jp.co.soramitsu.iroha2.transaction.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.Key;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import static jp.co.soramitsu.iroha2.ExtensionsKt.writeValue;

public class Transactions extends Constants {

    //TODO: It must keep running in a loop
    // EXECUTION DELAY after called the scn 10,5 sec
    public static ChainBuilder grantPermissionForTriggers = exec(feed(CSV_FEEDER))
            .exec(feed(PEERS_FEEDER))
            .exec(http("tx_grand_permission")
                    .post(session -> {
                                return session.getString("peer") + URL_TRANSACTION;
                            }
                    )
                    .body(ByteArrayBody(session -> {
                                        return SignedTransaction.Companion.encode(
                                                new TransactionBuilder(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                                                        .addInstructions(
                                                                Grant.Companion.accountPermission(
                                                                        new CanModifyAccountMetadata(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender"))),
                                                                        ExtensionsKt.asAccountId(ALICE_ACCOUNT + "@" + DEFAULT_DOMAIN)
                                                                ),
                                                                Grant.Companion.accountPermission(
                                                                        new CanTransferAsset(ExtensionsKt.asAssetId(session.getString("anotherDevAssetIdSender"))),
                                                                        ExtensionsKt.asAccountId(ALICE_ACCOUNT + "@" + DEFAULT_DOMAIN)
                                                                )
                                                        ).signAs(
                                                                ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")),
                                                                CryptoUtils.keyPairFromHex(
                                                                        session.getString("publicKeySender"),
                                                                        session.getString("privateKeySender")
                                                                )
                                                        )
                                        );

                                    }
                            )
                    )
            );

    public static ChainBuilder buySomeBondsBondAssetTrigger = exec(feed(CSV_FEEDER))
            .exec(feed(PEERS_FEEDER))
            .exec(feed(NEW_BOND_IDS_RC_20_BUY_BOND_TRIGGERS_TEST_CIRCULAR))
            .exec(http("tx_buy_bonds")
                    .post(session -> {
                                return session.getString("peer") + URL_TRANSACTION;
                            }
                    )
                    .body(ByteArrayBody(session -> {
                                        final var map = new HashMap<Name, Json>();
                                        map.put(ExtensionsKt.asName("bond"),
                                                new Json(session.getString("bondId")));

                                        map.put(ExtensionsKt.asName("quantity"),
                                                new Json("2"));

                                        map.put(ExtensionsKt.asName("transaction_type"),
                                                new Json(TransactionType.BOND_ASSET_PURCHASE.name()));

                                        final var metadata = new Metadata(map);

                                        return SignedTransaction.Companion.encode(
                                                new TransactionBuilder(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                                                .addInstructions(
                                                        new SetKeyValueOfAccount(
                                                                ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")),
                                                                ExtensionsKt.asName("buy_bonds"),
                                                                map.get("bond")
                                                        ),
                                                        new SetKeyValueOfAccount(
                                                                ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")),
                                                                ExtensionsKt.asName("buy_bonds"),
                                                                map.get("quantity")
                                                        )
                                                ).signAs(
                                                        ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")),
                                                        CryptoUtils.keyPairFromHex(
                                                                session.getString("publicKeySender"),
                                                                session.getString("privateKeySender")
                                                        )
                                                )
                                        );
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
                                                Map<Name, Json> map = new HashMap<Name, Json>();
                                                map.put(
                                                        ExtensionsKt.asName("dummy"),
                                                        new Json("1")
                                                );

                                                return SignedTransaction.Companion.encode(
                                                        new TransactionBuilder(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                                                                .addInstructions(
                                                                        new SetKeyValueOfAccount(
                                                                                ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")),
                                                                                ExtensionsKt.asName("buy_bonds"),
                                                                                map.get("dummy")
                                                                        )
                                                                ).signAs(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")),
                                                                        CryptoUtils.keyPairFromHex(
                                                                                session.getString("publicKeySender"),
                                                                                session.getString("privateKeySender")
                                                                        )
                                                                )
                                                );
                                            }
                                    )
                            )
                    );

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
                                                final var map = new HashMap<Name, Json>();
                                                map.put(ExtensionsKt.asName("bond"),
                                                        new Json(session.getString("assetDefinitionIdForTrigger")));

                                                map.put(ExtensionsKt.asName("quantity"),
                                                        new Json("2"));

                                                map.put(ExtensionsKt.asName("transaction_type"),
                                                        new Json(TransactionType.BOND_REDEMPTION_PAYMENT.name()));

                                        return SignedTransaction.Companion.encode(
                                                new TransactionBuilder(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                                                        .addInstructions(
                                                                new SetKeyValueOfAccount(
                                                                        ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")),
                                                                        ExtensionsKt.asName("redeem_bonds"),
                                                                        map.get("bond")
                                                                ),
                                                                new SetKeyValueOfAccount(
                                                                        ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")),
                                                                        ExtensionsKt.asName("redeem_bonds"),
                                                                        map.get("quantity")
                                                                ),
                                                                new SetKeyValueOfAccount(
                                                                        ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")),
                                                                        ExtensionsKt.asName("redeem_bonds"),
                                                                        map.get("transaction_type")
                                                                )
                                                        ).signAs(
                                                                ExtensionsKt.asAccountId(ALICE_ACCOUNT),
                                                                ALICE_KEYPAIR
                                                        )
                                                );

                                            }
                                    )
                            )
                    );

    //TODO: Must be generated:
    //      assetDefinitionIdSender
    public static ChainBuilder postRegisterDefinitionId = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(
                    http("tx_register_definition_id")
                            .post(session -> {
                                        return session.getString("peer") + URL_TRANSACTION;
                                    }
                            )
                            .body(ByteArrayBody(session -> {
                                                return SignedTransaction.Companion.encode(
                                                        new TransactionBuilder(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                                                                .addInstructions(
                                                                        Register.Companion.assetDefinition(
                                                                                ExtensionsKt.asAssetDefinitionId(session.getString("")),
                                                                                new AssetType.Numeric(new NumericSpec()),
                                                                                new Mintable.Infinitely(),
                                                                                new IpfsPath(null),
                                                                                new Metadata(new HashMap<Name, Json>())
                                                                        )
                                                                ).signAs(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")),
                                                                        CryptoUtils.keyPairFromHex(
                                                                                session.getString("publicKeySender"),
                                                                                session.getString("privateKeySender")
                                                                        )
                                                                )
                                                );
                                            }
                                    )
                            )
            ).exec(http("tx_register_definition_id_status").get(URL_STATUS).check(status().is(200)));

    //TODO: Must be generated:
    //      assetId
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
                                                        new TransactionBuilder(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                                                                .addInstructions(
                                                                        Mint.Companion.asset(
                                                                                ExtensionsKt.asAssetId(session.getString("")),
                                                                                new BigDecimal(10_000_000)
                                                                        )
                                                                ).signAs(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")),
                                                                        CryptoUtils.keyPairFromHex(
                                                                                session.getString("publicKeySender"),
                                                                                session.getString("privateKeySender")
                                                                        )
                                                                )
                                                );
                                            }
                                    )
                            )
                    );

    //TODO: must be generated special csv:
    //      domainId x3
    //      accountId
    //      assetDefinition x4
    //      assetID x10 for each ${assetDefinition}
    //      transfer - accountIdSender/accountIdReceiver
    public static ChainBuilder postMultiInstructions = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER)).exec(feed(MULTI_TXS_FEEDER))
            .exec(
                    http("tx_multi_instruction")
                            .post(session -> {
                                        return session.getString("peer") + URL_TRANSACTION;
                                    }
                            )
                            .body(ByteArrayBody(session -> {
                                                return SignedTransaction.Companion.encode(
                                                        new TransactionBuilder(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                                                                .addInstructions(
                                                                        Register.Companion.domain(ExtensionsKt.asDomainId(session.getString("")), new HashMap<Name, Json>(), new IpfsPath(null)),

                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),

                                                                        Register.Companion.domain(ExtensionsKt.asDomainId(session.getString("")), new HashMap<Name, Json>(), new IpfsPath(null)),

                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),

                                                                        Register.Companion.domain(ExtensionsKt.asDomainId(session.getString("")), new HashMap<Name, Json>(), new IpfsPath(null)),

                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),
                                                                        Register.Companion.account(ExtensionsKt.asAccountId(session.getString("")), new Metadata(new HashMap<Name, Json>())),

                                                                        Register.Companion.assetDefinition(ExtensionsKt.asAssetDefinitionId(session.getString("")), new AssetType.Numeric(new NumericSpec()), new Mintable.Infinitely(), new IpfsPath(null), new Metadata(new HashMap<Name, Json>())),

                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),

                                                                        Register.Companion.assetDefinition(ExtensionsKt.asAssetDefinitionId(session.getString("")), new AssetType.Numeric(new NumericSpec()), new Mintable.Infinitely(), new IpfsPath(null), new Metadata(new HashMap<Name, Json>())),

                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),

                                                                        Register.Companion.assetDefinition(ExtensionsKt.asAssetDefinitionId(session.getString("")), new AssetType.Numeric(new NumericSpec()), new Mintable.Infinitely(), new IpfsPath(null), new Metadata(new HashMap<Name, Json>())),

                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),

                                                                        Register.Companion.assetDefinition(ExtensionsKt.asAssetDefinitionId(session.getString("")), new AssetType.Numeric(new NumericSpec()), new Mintable.Infinitely(), new IpfsPath(null), new Metadata(new HashMap<Name, Json>())),

                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),
                                                                        Mint.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), new BigDecimal(10_000_000)),

                                                                        Transfer.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), BigDecimal.ONE, ExtensionsKt.asAccountId(session.getString(""))),
                                                                        Transfer.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), BigDecimal.ONE, ExtensionsKt.asAccountId(session.getString(""))),
                                                                        Transfer.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), BigDecimal.ONE, ExtensionsKt.asAccountId(session.getString(""))),
                                                                        Transfer.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), BigDecimal.ONE, ExtensionsKt.asAccountId(session.getString(""))),
                                                                        Transfer.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), BigDecimal.ONE, ExtensionsKt.asAccountId(session.getString(""))),
                                                                        Transfer.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), BigDecimal.ONE, ExtensionsKt.asAccountId(session.getString(""))),
                                                                        Transfer.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), BigDecimal.ONE, ExtensionsKt.asAccountId(session.getString(""))),
                                                                        Transfer.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), BigDecimal.ONE, ExtensionsKt.asAccountId(session.getString(""))),
                                                                        Transfer.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), BigDecimal.ONE, ExtensionsKt.asAccountId(session.getString(""))),
                                                                        Transfer.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), BigDecimal.ONE, ExtensionsKt.asAccountId(session.getString(""))),
                                                                        Transfer.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), BigDecimal.ONE, ExtensionsKt.asAccountId(session.getString(""))),
                                                                        Transfer.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), BigDecimal.ONE, ExtensionsKt.asAccountId(session.getString(""))),
                                                                        Transfer.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), BigDecimal.ONE, ExtensionsKt.asAccountId(session.getString(""))),
                                                                        Transfer.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), BigDecimal.ONE, ExtensionsKt.asAccountId(session.getString(""))),
                                                                        Transfer.Companion.asset(ExtensionsKt.asAssetId(session.getString("")), BigDecimal.ONE, ExtensionsKt.asAccountId(session.getString("")))
                                                                )
                                                                .signAs(ExtensionsKt.asAccountId(ALICE_ACCOUNT), ALICE_KEYPAIR)
                                                );
                                            }
                                    )
                            )
            ).exec(http("tx_multi_instruction_status").get(URL_STATUS).check(status().is(200)));

    //TODO: must be generated special csv:
    //      domainId x3
    public static ChainBuilder postRevertWorldViewMultiInstructions = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER)).exec(feed(MULTI_TXS_FEEDER))
            .exec(
                    http("tx_unregister_domain")
                            .post(session -> {
                                        return session.getString("peer") + URL_TRANSACTION;
                                    }
                            )
                            .body(ByteArrayBody(session -> {
                                                return SignedTransaction.Companion.encode(
                                                        new TransactionBuilder(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                                                                .addInstructions(
                                                                        Unregister.Companion.domain(ExtensionsKt.asDomainId("")),
                                                                        Unregister.Companion.domain(ExtensionsKt.asDomainId("")),
                                                                        Unregister.Companion.domain(ExtensionsKt.asDomainId(""))
                                                                ).signAs(ExtensionsKt.asAccountId(ALICE_ACCOUNT), ALICE_KEYPAIR)
                                                );
                                            }
                                    )
                            )
            ).exec(http("tx_unregister_domain_status").get(URL_STATUS).check(status().is(200)));

    //TODO: must be generated special csv:
    //      assetDefinitionId x4
    public static ChainBuilder postUnregisterDefinitionId = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(
                    http("tx_register_definition_id")
                            .post(session -> {
                                        return session.getString("peer") + URL_TRANSACTION;
                                    }
                            )
                            .body(ByteArrayBody(session -> {
                                                return SignedTransaction.Companion.encode(
                                                        new TransactionBuilder(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                                                                .addInstructions(
                                                                        Unregister.Companion.assetDefinition(ExtensionsKt.asAssetDefinitionId(session.getString(""))),
                                                                        Unregister.Companion.assetDefinition(ExtensionsKt.asAssetDefinitionId(session.getString(""))),
                                                                        Unregister.Companion.assetDefinition(ExtensionsKt.asAssetDefinitionId(session.getString(""))),
                                                                        Unregister.Companion.assetDefinition(ExtensionsKt.asAssetDefinitionId(session.getString("")))
                                                                ).signAs(ExtensionsKt.asAccountId(ALICE_ACCOUNT), ALICE_KEYPAIR)
                                                );
                                            }
                                    )
                            )
            ).exec(http("tx_register_definition_id_status").get(URL_STATUS).check(status().is(200)));

    //TODO: must be generated special csv:
    //      domainId x3
    public static ChainBuilder postRegisterDomain = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER))
            .exec(
                    http("tx_register_domain")
                            .post(session -> {
                                        return session.getString("peer") + URL_TRANSACTION;
                                    }
                            )
                            .body(ByteArrayBody(session -> {
                                                return SignedTransaction.Companion.encode(
                                                        new TransactionBuilder(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                                                                .addInstructions(
                                                                        Register.Companion.domain(ExtensionsKt.asDomainId(session.getString("")), new HashMap<Name, Json>(), new IpfsPath(null)),
                                                                        Register.Companion.domain(ExtensionsKt.asDomainId(session.getString("")), new HashMap<Name, Json>(), new IpfsPath(null)),
                                                                        Register.Companion.domain(ExtensionsKt.asDomainId(session.getString("")), new HashMap<Name, Json>(), new IpfsPath(null))

                                                                ).signAs(ExtensionsKt.asAccountId(ALICE_ACCOUNT), ALICE_KEYPAIR)
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
                            ).body(
                                    ByteArrayBody(
                                            session -> {
                                                return SignedTransaction.Companion.encode(
                                                        new TransactionBuilder(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                                                                .addInstructions(
                                                                        Transfer.Companion.asset(
                                                                                ExtensionsKt.asAssetId(session.getString("anotherDevAssetIdSender")),
                                                                                BigDecimal.ONE,
                                                                                ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdReceiver"))
                                                                        )
                                                                ).signAs(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")),
                                                                        CryptoUtils.keyPairFromHex(
                                                                                session.getString("publicKeySender"),
                                                                                session.getString("privateKeySender")
                                                                        )
                                                                )
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
                                                        new TransactionBuilder(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                                                                .addInstructions(
                                                                        Transfer.Companion.asset(
                                                                                ExtensionsKt.asAssetId(session.getString("anotherDevAssetIdSender")),
                                                                                BigDecimal.ONE,
                                                                                ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdReceiver"))
                                                                        )
                                                                ).signAs(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")),
                                                                        CryptoUtils.keyPairFromHex(
                                                                                session.getString("publicKeySender"),
                                                                                session.getString("privateKeySender")
                                                                        )
                                                                )
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
