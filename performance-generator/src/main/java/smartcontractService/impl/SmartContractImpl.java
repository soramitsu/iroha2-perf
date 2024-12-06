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
        final var triggerId = new TriggerId(ExtensionsKt.asName(buyBondId));

        final var wasm = Objects
                .requireNonNull(getClass().getClassLoader().getResource(buyBondWasm))
                .openConnection()
                .getInputStream()
                .readAllBytes();

        final var filter = Filters.INSTANCE.data(
                EntityFilters.INSTANCE.byAccount(
                        1L,
                        ExtensionsKt.asAccountId("ed0120CE7FA46C9DCE7EA4B125E2E36BDB63EA33073E7590AC92816AE1E861B7048B03@wonderland")
                )
        );

        return buildAndSubmitRegisterTriggerTransaction(triggerId, wasm, filter);
    }

    @SneakyThrows
    private SignedTransaction registerRedeemBondsTrigger(String redeemBondId, String redeemBondWasm) {
        final var triggerId = new TriggerId(ExtensionsKt.asName(redeemBondId));

        final var wasm = Objects
                .requireNonNull(getClass().getClassLoader().getResource(redeemBondWasm))
                .openConnection()
                .getInputStream()
                .readAllBytes();

        final var filter = Filters.INSTANCE.data(
                EntityFilters.INSTANCE.byAccount(
                        1L,
                        ExtensionsKt.asAccountId("ed0120CE7FA46C9DCE7EA4B125E2E36BDB63EA33073E7590AC92816AE1E861B7048B03@wonderland")
                )
        );

        return buildAndSubmitRegisterTriggerTransaction(triggerId, wasm, filter);
    }

    @SneakyThrows
    private SignedTransaction registerRegisterBondTrigger(String registerBondId, String registerBondWasm) {
        final var triggerId = new TriggerId(ExtensionsKt.asName(registerBondId));

        final var filter = buildFilterByTriggerMetadataInserted(triggerId);

        final var wasm = Objects
                .requireNonNull(getClass().getClassLoader().getResource(registerBondWasm))
                .openConnection()
                .getInputStream()
                .readAllBytes();

        return buildAndSubmitRegisterTriggerTransaction(triggerId, wasm, filter);
    }

    private SignedTransaction buildAndSubmitRegisterTriggerTransaction(TriggerId triggerId, byte[] wasm, EventFilterBox filter) {
        final var wasmTrigger = new TransactionBuilder(CHAIN_ID).addInstruction(
                        new RegisterOfTrigger(
                                new Trigger(
                                        triggerId,
                                        new Action(
                                                new Executable.Wasm(new WasmSmartContract(wasm)),
                                                new Repeats.Indefinitely(),
                                                ExtensionsKt.asAccountId(ALICE_ACCOUNT_RC20_ID),
                                                filter,
                                                new Metadata(Map.of())
                                        )
                                ))
                ).signAs(ExtensionsKt.asAccountId(ALICE_ACCOUNT_RC20_ID), ALICE_KEYPAIR);
        return wasmTrigger;
    }

    private EventFilterBox.Data buildFilterByTriggerMetadataInserted(TriggerId id) {
        return new EventFilterBox.Data(new DataEventFilter.Trigger(new TriggerEventFilter(id, 16)));
    }
}
