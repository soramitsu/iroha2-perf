package healper.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import configs.tests.PalauProperties;
import healper.BuildInfo;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.generated.*;
import jp.co.soramitsu.iroha2.transaction.EntityFilters;
import jp.co.soramitsu.iroha2.transaction.Filters;
import jp.co.soramitsu.iroha2.transaction.TransactionBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import objects.SmartContractService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import requests.Constants;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class SmartContractImpl extends Constants implements SmartContractService {

    private final PalauProperties palauProperties;

    private final PasswordEncoder passwordEncoder;

    private final BuildInfo buildInfo;

    @SneakyThrows
    public SmartContractImpl (PasswordEncoder passwordEncoder, ObjectMapper objectMapper, PalauProperties palauProperties) {
        this.passwordEncoder = passwordEncoder;
        this.palauProperties = palauProperties;

        this.buildInfo = objectMapper.readValue(
                this.getClass().getClassLoader()
                        .getResource(palauProperties.getTrigger().getBuildInfoPath())
                        .openConnection()
                        .getInputStream()
                        .readAllBytes(), BuildInfo.class);

        log.info("Palau smart contracts' build info: {}", buildInfo);
    }

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
}
