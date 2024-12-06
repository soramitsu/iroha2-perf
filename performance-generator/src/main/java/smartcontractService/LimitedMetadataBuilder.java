package smartcontractService;

import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.generated.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class LimitedMetadataBuilder {

    private final Map<Name, Json> metadataMap;

    private LimitedMetadataBuilder() {
        this.metadataMap = new HashMap<>();
    }

    public static LimitedMetadataBuilder builder() {
        return new LimitedMetadataBuilder();
    }

    public LimitedMetadataBuilder withCurrency(AssetDefinitionId currency) {
        metadataMap.put(
                ExtensionsKt.asName("currency"),
                ExtensionsKt.writeValue(Json.Companion, currency)
        );
        return this;
    }

    public LimitedMetadataBuilder withNominalValue(BigDecimal nominalValue) {
        metadataMap.put(
                ExtensionsKt.asName("nominal_value"),
                ExtensionsKt.writeValue(Json.Companion, nominalValue.toString())
        );
        return this;
    }

    public LimitedMetadataBuilder withCouponRate(BigDecimal couponRate) {
        metadataMap.put(
                ExtensionsKt.asName("coupon_rate"),
                ExtensionsKt.writeValue(Json.Companion, couponRate.toString())
        );
        return this;
    }

    public LimitedMetadataBuilder withPaymentFrequencySeconds(Long paymentFrequencySeconds) {
        metadataMap.put(
                ExtensionsKt.asName("payment_frequency_seconds"),
                ExtensionsKt.writeValue(Json.Companion, paymentFrequencySeconds.toString())
        );
        return this;
    }

    public LimitedMetadataBuilder withFixedFee(BigDecimal fixedFee) {
        metadataMap.put(
                ExtensionsKt.asName("fixed_fee"),
                ExtensionsKt.writeValue(Json.Companion, fixedFee.toString())
        );
        return this;
    }

    public LimitedMetadataBuilder withFeeRecipientAccountId(AccountId feeRecipientAccountId) {
        metadataMap.put(
                ExtensionsKt.asName("fee_recipient_account_id"),
                ExtensionsKt.writeValue(Json.Companion, feeRecipientAccountId)
        );
        return this;
    }

    public LimitedMetadataBuilder withMaturityDateMs(Long maturityDateMs) {
        metadataMap.put(
                ExtensionsKt.asName("maturation_date_ms"),
                ExtensionsKt.writeValue(Json.Companion, maturityDateMs.toString())
        );
        return this;
    }

    public LimitedMetadataBuilder withRegistrationDateMs(Long registrationDateMs) {
        metadataMap.put(
                ExtensionsKt.asName("registration_time_ms"),
                ExtensionsKt.writeValue(Json.Companion, registrationDateMs.toString())
        );
        return this;
    }

    public LimitedMetadataBuilder withQuantity(Integer quantity) {
        metadataMap.put(
                ExtensionsKt.asName("quantity"),
                ExtensionsKt.writeValue(Json.Companion, quantity.toString())
        );
        return this;
    }

    public Metadata build() {
        return new Metadata(metadataMap);
    }
}
