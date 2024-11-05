package smartcontractService;

import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.generated.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class LimitedMetadataBuilder {

    private final Map<Name, Value> metadataMap;

    private LimitedMetadataBuilder() {
        this.metadataMap = new HashMap<>();
    }

    public static LimitedMetadataBuilder builder() {
        return new LimitedMetadataBuilder();
    }

    public LimitedMetadataBuilder withCurrency(AssetDefinitionId currency) {
        metadataMap.put(
                ExtensionsKt.asName("currency"),
                ExtensionsKt.asValue(currency));

        return this;
    }

    public LimitedMetadataBuilder withNominalValue(BigDecimal nominalValue) {
        metadataMap.put(
                ExtensionsKt.asName("nominal_value"),
                ExtensionsKt.asValue(nominalValue));

        return this;
    }

    public LimitedMetadataBuilder withCouponRate(BigDecimal couponRate) {
        metadataMap.put(
                ExtensionsKt.asName("coupon_rate"),
                ExtensionsKt.asValue(couponRate));

        return this;
    }

    public LimitedMetadataBuilder withPaymentFrequencySeconds(long paymentFrequencySeconds) {
        metadataMap.put(
                ExtensionsKt.asName("payment_frequency_seconds"),
                ExtensionsKt.asValue(paymentFrequencySeconds));

        return this;
    }

    public LimitedMetadataBuilder withFixedFee(BigDecimal fixedFee) {
        metadataMap.put(
                ExtensionsKt.asName("fixed_fee"),
                ExtensionsKt.asValue(fixedFee));

        return this;
    }

    public LimitedMetadataBuilder withFeeRecipientAccountId(AccountId feeRecipientAccountId) {
        metadataMap.put(
                ExtensionsKt.asName("fee_recipient_account_id"),
                ExtensionsKt.asValue(feeRecipientAccountId));

        return this;
    }

    public LimitedMetadataBuilder withMaturityDateMs(long maturityDateMs) {
        metadataMap.put(
                ExtensionsKt.asName("maturation_date_ms"),
                ExtensionsKt.asValue(maturityDateMs));

        return this;
    }

    public LimitedMetadataBuilder withRegistrationDateMs(long registrationDateMs) {
        metadataMap.put(
                ExtensionsKt.asName("registration_time_ms"),
                ExtensionsKt.asValue(registrationDateMs));

        return this;
    }

    public LimitedMetadataBuilder withQuantity(int quantity) {
        metadataMap.put(
                ExtensionsKt.asName("quantity"),
                ExtensionsKt.asValue(quantity)
        );

        return this;
    }

    public Value.LimitedMetadata build() {
        Metadata metadata = new Metadata(metadataMap);

        return new Value.LimitedMetadata(metadata);
    }
}
