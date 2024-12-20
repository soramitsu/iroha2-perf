package smartcontractService.impl;

import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.generated.*;
import jp.co.soramitsu.iroha2.transaction.EntityFilters;
import jp.co.soramitsu.iroha2.transaction.Filters;
import jp.co.soramitsu.iroha2.transaction.TransactionBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import configs.tests.PalauProperties;
import smartcontractService.Constants;
import smartcontractService.SmartContractService;

import java.util.Map;
import java.util.Objects;

@Slf4j
public class SmartContractImpl extends Constants implements SmartContractService {

    private final PalauProperties palauProperties;

    @SneakyThrows
    public SmartContractImpl(PalauProperties palauProperties) {
        this.palauProperties = palauProperties;
    }

    @Override
    public SignedTransaction deployRegisterRegisterBondTrigger(String activationCode) {
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
}
