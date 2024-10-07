package healper;

import Configs.Tests.PalauProperties;
import jp.co.soramitsu.domain.transfer.enums.TransactionType;
import jp.co.soramitsu.exceptions.ClientErrorException;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.ModelEnum;
import jp.co.soramitsu.iroha2.generated.*;
import jp.co.soramitsu.iroha2.transaction.EntityFilters;
import jp.co.soramitsu.iroha2.transaction.Filters;
import jp.co.soramitsu.iroha2.transaction.TransactionBuilder;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import objects.BondService;
import objects.CreateBond;
import objects.SmartContractService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import io.gatling.javaapi.core.Session;
import requests.Constants;

import java.util.Map;
import java.util.Objects;

import static jp.co.soramitsu.core.iroha2.util.MetadataUtil.enrichMetadata;
import static jp.co.soramitsu.core.iroha2.util.TransactionUtil.TRANSACTION_TYPE_METADATA_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceMethod extends Constants implements BondService, SmartContractService  {

    private final PalauProperties palauProperties;

    private final PasswordEncoder passwordEncoder;

    private final BuildInfo buildInfo;

    @Override
    public SignedTransaction deployRegisterRegisterBondTrigger(String activationCode) {
        if (!passwordEncoder.matches(activationCode, buildInfo.activationCode())) {
            throw new RuntimeException("Invalid activation code");
        }

        log.info("""
                Deploying smart contracts:
                {}
                """,
                palauProperties.getTrigger().getRegisterBondWasm());

        return registerRegisterBondTrigger(
                palauProperties.getTrigger().getRegisterBondId(),
                palauProperties.getTrigger().getRegisterBondWasm());
    }

    @Override
    public SignedTransaction deployRegisterBuyBondsTrigger(String activationCode) {
        if (!passwordEncoder.matches(activationCode, buildInfo.activationCode())) {
            throw new RuntimeException("Invalid activation code");
        }

        log.info("""
                Deploying smart contracts:
                {}
                """,
                palauProperties.getTrigger().getBuyBondWasm());

        return registerBuyBondsTrigger(
                palauProperties.getTrigger().getBuyBondId(),
                palauProperties.getTrigger().getBuyBondWasm());
    }

    @Override
    public SignedTransaction deployRegisterRedeemBondsTrigger(String activationCode) {
        if (!passwordEncoder.matches(activationCode, buildInfo.activationCode())) {
            throw new RuntimeException("Invalid activation code");
        }

        log.info("""
                Deploying smart contracts:
                {}
                """,
                palauProperties.getTrigger().getRedeemBondWasm());

        return registerRedeemBondsTrigger(
                palauProperties.getTrigger().getRedeemBondId(),
                palauProperties.getTrigger().getRedeemBondWasm());
    }

    @SneakyThrows
    private SignedTransaction registerBuyBondsTrigger(String buyBondId, String buyBondWasm) {
        final var triggerId = new TriggerId(null, ExtensionsKt.asName(buyBondId));

        final var wasm = Objects
                .requireNonNull(getClass().getClassLoader().getResource(buyBondWasm))
                .openConnection()
                .getInputStream()
                .readAllBytes();

        final var filter = Filters.INSTANCE.data(
                EntityFilters.INSTANCE.byAccount(
                        null,
                        new AccountEventFilter.ByMetadataInserted()));

        return buildAndSubmitRegisterTriggerTransaction(triggerId, wasm, filter);
    }

    @SneakyThrows
    private SignedTransaction registerRedeemBondsTrigger(String redeemBondId, String redeemBondWasm) {
        final var triggerId = new TriggerId(null, ExtensionsKt.asName(redeemBondId));

        final var wasm = Objects
                .requireNonNull(getClass().getClassLoader().getResource(redeemBondWasm))
                .openConnection()
                .getInputStream()
                .readAllBytes();

        final var filter = Filters.INSTANCE.data(
                EntityFilters.INSTANCE.byAccount(
                        null,
                        new AccountEventFilter.ByMetadataInserted()));

        return buildAndSubmitRegisterTriggerTransaction(triggerId, wasm, filter);
    }

    @SneakyThrows
    private SignedTransaction registerRegisterBondTrigger(String registerBondId, String registerBondWasm) {
        final var triggerId = new TriggerId(null, ExtensionsKt.asName(registerBondId));

        final var filter = buildFilterByTriggerMetadataInserted(triggerId);

        final var wasm = Objects
                .requireNonNull(getClass().getClassLoader().getResource(registerBondWasm))
                .openConnection()
                .getInputStream()
                .readAllBytes();

        return buildAndSubmitRegisterTriggerTransaction(triggerId, wasm, filter);
    }

    private SignedTransaction buildAndSubmitRegisterTriggerTransaction(TriggerId triggerId, byte[] wasm, TriggeringFilterBox.Data filter) {
        final var wasmTrigger = TransactionBuilder.Companion.builder()
                .registerWasmTrigger(
                        triggerId,
                        wasm,
                        new Repeats.Indefinitely(),
                        ExtensionsKt.asAccountId(ALICE_ACCOUNT_RC20_ID),
                        new Metadata(Map.of()),
                        filter)
                .account(ExtensionsKt.asAccountId(ALICE_ACCOUNT_RC20_ID))
                .buildSigned(ALICE_KEYPAIR);
        return wasmTrigger;
    }

    private TriggeringFilterBox.Data buildFilterByTriggerMetadataInserted(TriggerId id) {
        final var orot = new OriginFilterOfTriggerEvent(id);
        final var byMetadataInserted = new TriggerEventFilter.ByMetadataInserted();

        final var orotWrapper = new FilterOptOfOriginFilterOfTriggerEvent.BySome(orot);
        final var byMetadataWrapper = new FilterOptOfTriggerEventFilter.BySome(byMetadataInserted);

        final var triggerFilter = new TriggerFilter(orotWrapper, byMetadataWrapper);
        final var triggerFilterWrapper = new FilterOptOfTriggerFilter.BySome(triggerFilter);

        final var filter = new DataEntityFilter.ByTrigger(triggerFilterWrapper);
        final var bySomeFilter = new FilterOptOfDataEntityFilter.BySome(filter);

        return new TriggeringFilterBox.Data(bySomeFilter);
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
