package healper.impl;

import configs.tests.PalauProperties;
import healper.LimitedMetadataBuilder;
import io.gatling.javaapi.core.Session;
import jp.co.soramitsu.domain.transfer.enums.TransactionType;
import jp.co.soramitsu.exceptions.ClientErrorException;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.generated.*;
import jp.co.soramitsu.iroha2.transaction.TransactionBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import objects.BondService;
import objects.CreateBond;
import org.springframework.stereotype.Service;
import requests.Constants;

import static jp.co.soramitsu.core.iroha2.util.MetadataUtil.enrichMetadata;
import static jp.co.soramitsu.core.iroha2.util.TransactionUtil.TRANSACTION_TYPE_METADATA_KEY;

@Slf4j
@Service
public class BondImpl extends Constants implements BondService  {

    private final PalauProperties palauProperties;

    @SneakyThrows
    public BondImpl(PalauProperties palauProperties) {
        this.palauProperties = palauProperties;
    }

    @Override
    public SignedTransaction getSignedRegisterBondAssetTx(CreateBond createBond, Session session) throws ClientErrorException {
        final var bondMetadata = buildBondMetadata(createBond);
        final var newAssetDefinition = new NewAssetDefinition(
                createBond.bondId(),
                new AssetValueType.Quantity(),
                new Mintable.Infinitely(),
                null,
                bondMetadata.getMetadata());

        final var triggerId = new TriggerId(
                null, // Triggers with `null` domain are considered as "global" triggers
                ExtensionsKt.asName(palauProperties.getTrigger().getRegisterBondId()));

        final var registerBond = TransactionBuilder.Companion.builder()
                .setKeyValue(
                        triggerId,
                        ExtensionsKt.asName(palauProperties.getTrigger().getRegisterBondTriggerKey()),
                        ExtensionsKt.asValue(newAssetDefinition))
                .account(ExtensionsKt.asAccountId(session.getString("anotherDevAccountIdSender")));

        enrichMetadata(registerBond, TRANSACTION_TYPE_METADATA_KEY,
                TransactionType.BOND_ASSET_ISSUE.name());

        return registerBond.buildSigned(ALICE_KEYPAIR);
    }

    private Value.LimitedMetadata buildBondMetadata(CreateBond createBond) {
        return LimitedMetadataBuilder.builder()
                .withCurrency(createBond.currency())
                .withNominalValue(createBond.nominalValue())
                .withCouponRate(createBond.couponRate())
                .withPaymentFrequencySeconds(createBond.paymentFrequencySeconds())
                .withFixedFee(createBond.feeAmount())
                .withFeeRecipientAccountId(createBond.feeRecipient())
                .withMaturityDateMs(createBond.maturityDateMs())
                .withRegistrationDateMs(createBond.registrationDateMs())
                .withQuantity(createBond.quantity())
                .build();
    }
}
