package requests;

import configs.tests.PalauProperties;
import healper.Constants;
import healper.impl.BondImpl;
import healper.impl.SmartContractImpl;
import io.gatling.javaapi.core.ChainBuilder;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.generated.AssetDefinitionId;
import jp.co.soramitsu.iroha2.generated.SignedTransaction;
import objects.Bond;

import java.math.BigDecimal;
import java.time.Duration;

import static healper.Constants.*;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class Triggers extends PalauProperties {

    private static BondImpl bond = new BondImpl(new PalauProperties());

    private static SmartContractImpl smartContract = new SmartContractImpl(new PalauProperties());

    // it must be used onceOnly() controller
    // DONE добавить в генезис в ассетдефинишн метадату /Users/michaeltimofeev/Documents/projects/laos-iroha2-adapter/docker-compose/palau/genesis.json
    // наминтить ассет
    // после стартавать смартконтракты

    public static ChainBuilder registerBondAssetTrigger = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER)).exec(feed(MULTI_TXS_FEEDER))
            // it must use onlyOne() controller
            .exec(http("register bond_asset trigger")
                    .post(session -> {
                                return "/peer-0/" + Constants.URL_TRANSACTION;
                            }
                    )
                    .body(ByteArrayBody(session -> {
                                        return SignedTransaction.Companion.encode(smartContract.deployRegisterRegisterBondTrigger("019153fc-8d19-7faa-854a-57657ba0ed20"));
                                    }
                            )
                    )
            );

    public static ChainBuilder registerBuyBondsTrigger = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER)).exec(feed(MULTI_TXS_FEEDER))
            // it must use onlyOne() controller
            .exec(http("register buy_bonds trigger")
                    .post(session -> {
                                return "/peer-0/" + Constants.URL_TRANSACTION;
                            }
                    )
                    .body(ByteArrayBody(session -> {
                                        return SignedTransaction.Companion.encode(smartContract.deployRegisterBuyBondsTrigger("019153fc-8d19-7faa-854a-57657ba0ed20"));
                                    }
                            )
                    )
            );

    public static ChainBuilder registerRedeemBondsTrigger = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER)).exec(feed(MULTI_TXS_FEEDER))
            // it must use onlyOne() controller
            .exec(http("register redeem_bonds trigger")
                    .post(session -> {
                                return "/peer-0/" + Constants.URL_TRANSACTION;
                            }
                    )
                    .body(ByteArrayBody(session -> {
                                        return SignedTransaction.Companion.encode(smartContract.deployRegisterRedeemBondsTrigger("019153fc-8d19-7faa-854a-57657ba0ed20"));
                                    }
                            )
                    )
            );

    public static ChainBuilder registerBondAsset =
            exec(feed(PEERS_FEEDER))
            .exec(feed(ACCOUNT_IDS_RC_20_TRIGGERS_TEST))
            .exec(feed(DOMAIN_IDS_RC_20_TRIGGERS_TEST))
            .exec(feed(ASSET_DEFINITION_IDS_RC_20_REGISTER_BOND_TRIGGERS_TEST))
            .exec(feed(NEW_BOND_IDS_RC_20_BUY_BOND_TRIGGERS_TEST))
            .exec(http("register bond asset")
                    .post(session -> {
                                return session.getString("peer") + Constants.URL_TRANSACTION;
                            }
                    )
                    .body(ByteArrayBody(session -> {
                                        final var currentTime = System.currentTimeMillis();
                                        final var currencyId = new AssetDefinitionId(
                                                ExtensionsKt.asName(session.getString("assetDefinitionIdForTrigger").split("#")[0]),
                                                ExtensionsKt.asDomainId(session.getString("assetDefinitionIdForTrigger").split("#")[1])
                                        );
                                        final var feeRecipientAccountId = ExtensionsKt.asAccountId(session.getString("accountIdForTrigger"));

                                        return SignedTransaction.Companion.encode(
                                                bond.getSignedRegisterBondAssetTx(Bond.builder()
                                                                .currency(currencyId)
                                                                .bondId(ExtensionsKt.asAssetDefinitionId(session.getString("bondId")))
                                                                .quantity(999999999)
                                                                .nominalValue(new BigDecimal("100000.00"))
                                                                .couponRate(new BigDecimal("0.1"))
                                                                .paymentFrequencySeconds(10L)
                                                                .feeAmount(new BigDecimal("0.1"))
                                                                .feeRecipient(feeRecipientAccountId)
                                                                .maturityDateMs(currentTime + Duration.ofSeconds(180).toMillis())
                                                                .registrationDateMs(currentTime)
                                                                .build()
                                                        , ALICE_ACCOUNT + "@" + DEFAULT_DOMAIN)
                                        );
                                    }
                            )
                    )
            );






}
