package smartcontractService.impl;

import jp.co.soramitsu.domain.transfer.enums.TransactionType;
import jp.co.soramitsu.exceptions.ClientErrorException;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.generated.*;
import jp.co.soramitsu.iroha2.transaction.TransactionBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import configs.tests.PalauProperties;
import smartcontractService.BondService;
import smartcontractService.Constants;
import smartcontractService.LimitedMetadataBuilder;
import objects.Bond;

import static jp.co.soramitsu.core.iroha2.util.MetadataUtil.enrichMetadata;
import static jp.co.soramitsu.core.iroha2.util.TransactionUtil.TRANSACTION_TYPE_METADATA_KEY;

@Slf4j
public class BondImpl extends Constants implements BondService {

    private final PalauProperties palauProperties;

    @SneakyThrows
    public BondImpl(PalauProperties palauProperties) {
        this.palauProperties = palauProperties;
    }

    @Override
    public SignedTransaction getSignedRegisterBondAssetTx(Bond bond, String accountId) throws ClientErrorException {
        final var bondMetadata = buildBondMetadata(bond);
        final var newAssetDefinition = new NewAssetDefinition(
                bond.bondId(),
                new AssetType.Numeric(new NumericSpec()),
                new Mintable.Infinitely(),
                null,
                bondMetadata);

        final var triggerId = new TriggerId(
                // Triggers with `null` domain are considered as "global" triggers
                ExtensionsKt.asName(palauProperties.getTrigger().getRegisterBondId())
        );

        final var registerBond = new TransactionBuilder(CHAIN_ID).addInstructions(
                new SetKeyValueOfTrigger(
                        triggerId,
                        ExtensionsKt.asName(palauProperties.getTrigger().getRegisterBondTriggerKey()),
                        ExtensionsKt.writeValue(Json.Companion, newAssetDefinition.component1()))
        );

        enrichMetadata(
                registerBond,
                TRANSACTION_TYPE_METADATA_KEY,
                TransactionType.BOND_ASSET_ISSUE.name()
        );

        return registerBond.signAs(ExtensionsKt.asAccountId(ALICE_ACCOUNT_RC20_ID), ALICE_KEYPAIR);
    }

    private Metadata buildBondMetadata(Bond bond) {

        return LimitedMetadataBuilder.builder()
                .withCurrency(bond.currency())
                .withNominalValue(bond.nominalValue())
                .withCouponRate(bond.couponRate())
                .withPaymentFrequencySeconds(bond.paymentFrequencySeconds())
                .withFixedFee(bond.feeAmount())
                .withFeeRecipientAccountId(bond.feeRecipient())
                .withMaturityDateMs(bond.maturityDateMs())
                .withRegistrationDateMs(bond.registrationDateMs())
                .withQuantity(bond.quantity())
                .build();
    }
}
