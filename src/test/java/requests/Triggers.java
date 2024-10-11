package requests;

import com.beust.ah.A;
import configs.tests.PalauProperties;
import configs.tests.AssetDefinitionIdGenerator;
import healper.Constants;
import healper.impl.BondImpl;
import healper.impl.SmartContractImpl;
import io.gatling.javaapi.core.ChainBuilder;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.generated.AssetDefinitionId;
import jp.co.soramitsu.iroha2.generated.SignedTransaction;
import objects.Bond;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;

import static healper.Constants.*;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class Triggers extends PalauProperties {

    private static BondImpl bond = new BondImpl(new PalauProperties());

    private static SmartContractImpl smartContract = new SmartContractImpl(new PalauProperties());

    public static ChainBuilder registerBondAsset =
            exec(feed(CSV_FEEDER))
            .exec(feed(PEERS_FEEDER))
            .exec(feed(ACCOUNT_IDS_RC_20_TRIGGERS_TEST))
            // it must use onlyOne() controller
            .exec(http("register bond asset")
                    .post(session -> {
                                return session.getString("peer") + Constants.URL_TRANSACTION;
                            }
                    )
                    .body(ByteArrayBody(session -> {
                                        final var currentTime = System.currentTimeMillis();
                                        final var currencyId = new AssetDefinitionId(ExtensionsKt.asName(session.getString("assetDefinitionIdSender")),
                                                ExtensionsKt.asDomainId(session.getString("domainIdSender")));
                                        final var feeRecipientAccountId = ExtensionsKt.asAccountId(session.getString("accountIdForTrigger"));
                                        //как передавать во всем тесте полученные домены?
                                        /*try {
                                            assetDefinitionIdList = new AssetDefinitionIdGenerator().getAssetDefinitionList(5);
                                            session.set("assetDefinitionIdList", assetDefinitionIdList);

                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }*/




                                        AssetDefinitionId newAssetDefinition = new AssetDefinitionId(ExtensionsKt.asName("perf_xor" + session.getString("domainIdSender").split("-")[0]),
                                                ExtensionsKt.asDomainId(session.getString("domainIdSender")));

                                        return SignedTransaction.Companion.encode(
                                                bond.getSignedRegisterBondAssetTx(Bond.builder()
                                                                .currency(currencyId)
                                                                .bondId(newAssetDefinition)
                                                                .quantity(999999999)
                                                                .nominalValue(new BigDecimal("100000.00"))
                                                                .couponRate(new BigDecimal("0.1"))
                                                                .paymentFrequencySeconds(10L)
                                                                .feeAmount(new BigDecimal("0.1"))
                                                                .feeRecipient(feeRecipientAccountId)
                                                                .maturityDateMs(currentTime + Duration.ofSeconds(180).toMillis())
                                                                .registrationDateMs(currentTime)
                                                                .build()
                                                        , session)
                                        );
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
}
