package requests;

import healper.ServiceMethod;
import io.gatling.javaapi.core.ChainBuilder;
import jakarta.annotation.PostConstruct;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.generated.AssetDefinitionId;
import jp.co.soramitsu.iroha2.generated.SignedTransaction;
import objects.BondService;
import objects.CreateBond;
import objects.SmartContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

@Component
@SpringBootTest
public class Triggers extends Constants {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private static BondService bondService;

    private static SmartContractService smartContractService;

    public Triggers() {
        this.smartContractService = context.getBean(SmartContractService.class);
    }

    public static ChainBuilder bondAssetRegister = exec(feed(CSV_FEEDER)).exec(feed(PEERS_FEEDER)).exec(feed(MULTI_TXS_FEEDER))
            // it must use onlyOne() controller
            .exec(http("register bond asset")
                    .post(session -> {
                                return "/peer-0/" + Constants.URL_TRANSACTION;
                            }
                    )
                    .body(ByteArrayBody(session -> {
                                        final var currentTime = System.currentTimeMillis();
                                        final var currencyId = new AssetDefinitionId(ExtensionsKt.asName(session.getString("assetDefinitionIdSender")),
                                                ExtensionsKt.asDomainId(session.getString("domainIdSender")));
                                        final var feeRecipientAccountId = ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender"));

                                        return SignedTransaction.Companion.encode(
                                                bondService.getSignedRegisterBondAssetTx(CreateBond.builder()
                                                                .currency(currencyId)
                                                                .bondId(ExtensionsKt.asAssetDefinitionId("bondAsset#palau"))
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
                                        return SignedTransaction.Companion.encode(smartContractService.deployRegisterBuyBondsTrigger("019153fc-8d19-7faa-854a-57657ba0ed20"));
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
                                        return SignedTransaction.Companion.encode(smartContractService.deployRegisterRedeemBondsTrigger("019153fc-8d19-7faa-854a-57657ba0ed20"));
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
                                        return SignedTransaction.Companion.encode(smartContractService.deployRegisterRegisterBondTrigger("019153fc-8d19-7faa-854a-57657ba0ed20"));
                                    }
                            )
                    )
            );
}
