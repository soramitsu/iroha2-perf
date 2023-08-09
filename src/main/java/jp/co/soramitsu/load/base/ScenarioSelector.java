package jp.co.soramitsu.load.base;

import io.gatling.javaapi.core.ScenarioBuilder;
import jp.co.soramitsu.load.base.scenarious.Iroha2SetUp;
import jp.co.soramitsu.load.base.scenarious.SimpleScenario;
import jp.co.soramitsu.load.base.scenarious.TransferAssetsQueryStatus;
import jp.co.soramitsu.load.base.scenarious.TransferAssetsTransactionStatus;
import org.jetbrains.annotations.NotNull;

import static jp.co.soramitsu.load.infrastructure.enums.ScenarioEnum.findByValue;

public class ScenarioSelector {
    @NotNull
    public static ScenarioBuilder getScenario(String scenario) {
        switch (findByValue(scenario)) {
            case IROHA_2_SET_UP:
                return Iroha2SetUp.apply();
            case TRANSFER_ASSETS_TRANSACTION_STATUS:
                return TransferAssetsTransactionStatus.apply();
            case TRANSFER_ASSETS_QUERY_STATUS:
        return TransferAssetsQueryStatus.apply();
        }
        return SimpleScenario.apply();
    }
}
