package requests;

import configs.tests.PalauProperties;
import healper.impl.BondImpl;
import healper.impl.SmartContractImpl;
import io.gatling.javaapi.core.ChainBuilder;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.generated.AssetDefinitionId;
import jp.co.soramitsu.iroha2.generated.SignedTransaction;
import objects.CreateBond;

import java.math.BigDecimal;
import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class Triggers extends Constants {

    private static BondImpl bondService = new BondImpl(new PalauProperties());

    private static SmartContractImpl smartContractImpl = new SmartContractImpl(new PalauProperties());

    public static ChainBuilder bondAssetRegister = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER)).exec(feed(MULTI_TXS_FEEDER))
            // it must use onlyOne() controller
            .exec(http("register bond asset")
                    .post(session -> {
                                return "/peer-0/" + Constants.URL_TRANSACTION;
                            }
                    )
                    .body(ByteArrayBody(session -> {
                                        final var currentTime = System.currentTimeMillis();
                                        //новый ассет id

                                        //
                                        final var currencyId = new AssetDefinitionId(ExtensionsKt.asName(session.getString("assetDefinitionIdSender")),
                                                ExtensionsKt.asDomainId(session.getString("domainIdSender")));
                                        final var feeRecipientAccountId = ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender"));

                                        return SignedTransaction.Companion.encode(
                                                bondService.getSignedRegisterBondAssetTx(CreateBond.builder()
                                                                .currency(currencyId)
                                                                //уникальный ассет дефинишен в дальнейшем будем использовать в покупке бонда или его удалении - не используй session.getString("assetDefinitionIdSender"))
                                                                //xor52ebff34-4715-4c1b-b41c-e8ab347e1dbb_4956d877-0133-4004-84f3-de4d08697dde#bulb_f642e79d-fa2c-429f-8a9a-4b5ceb65dc8c_9563a8dc-d0e0-413a-870b-a3921f956834
                                                                .bondId(ExtensionsKt.asAssetDefinitionId("xor_for_perf#bulb_f642e79d-fa2c-429f-8a9a-4b5ceb65dc8c_9563a8dc-d0e0-413a-870b-a3921f956834"))
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

    public static ChainBuilder buyBondsTriggerRegister = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER)).exec(feed(MULTI_TXS_FEEDER))
            // it must use onlyOne() controller
            .exec(http("register buy_bonds trigger")
                    .post(session -> {
                                return "/peer-0/" + Constants.URL_TRANSACTION;
                            }
                    )
                    .body(ByteArrayBody(session -> {
                                        return SignedTransaction.Companion.encode(smartContractImpl.deployRegisterBuyBondsTrigger("019153fc-8d19-7faa-854a-57657ba0ed20"));
                                    }
                            )
                    )
            );

    public static ChainBuilder redeemBondsTriggerRegister = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER)).exec(feed(MULTI_TXS_FEEDER))
            // it must use onlyOne() controller
            .exec(http("register redeem_bonds trigger")
                    .post(session -> {
                                return "/peer-0/" + Constants.URL_TRANSACTION;
                            }
                    )
                    .body(ByteArrayBody(session -> {
                                        return SignedTransaction.Companion.encode(smartContractImpl.deployRegisterRedeemBondsTrigger("019153fc-8d19-7faa-854a-57657ba0ed20"));
                                    }
                            )
                    )
            );

    public static ChainBuilder bondAssetTriggerRegister = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER)).exec(feed(MULTI_TXS_FEEDER))
            // it must use onlyOne() controller
            .exec(http("register bond_asset trigger")
                    .post(session -> {
                                return "/peer-0/" + Constants.URL_TRANSACTION;
                            }
                    )
                    .body(ByteArrayBody(session -> {
                                        return SignedTransaction.Companion.encode(smartContractImpl.deployRegisterRegisterBondTrigger("019153fc-8d19-7faa-854a-57657ba0ed20"));
                                    }
                            )
                    )
            );
}
