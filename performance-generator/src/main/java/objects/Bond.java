package objects;

import jp.co.soramitsu.iroha2.generated.AccountId;
import jp.co.soramitsu.iroha2.generated.AssetDefinitionId;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Bond(AssetDefinitionId currency,
                   AssetDefinitionId bondId,
                   BigDecimal nominalValue,
                   BigDecimal couponRate,
                   Integer quantity,
                   Long paymentFrequencySeconds,
                   BigDecimal feeAmount, // fee for bond acquisition. Fixed absolute value, for now
                   AccountId feeRecipient,
                   Long maturityDateMs,
                   Long registrationDateMs) {
}
