package jp.co.soramitsu.load;

import io.gatling.javaapi.core.ScenarioBuilder;
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig;
import jp.co.soramitsu.load.infrastructure.enums.ScenarioEnum;
import org.jetbrains.annotations.NotNull;

public class ScenarioSelector {

    @NotNull
    public static ScenarioBuilder getScenario() {
        switch (ScenarioEnum.findByValue(SimulationConfig.simulation.getScn())) {
            case IROHA_2_SET_UP:
                return Iroha2SetUp.Companion.apply();
            /*case TRANSFER_ASSETS_TRANSACTION_STATUS:
                return TransferAssetsTransactionStatus.apply();*/
            case TRANSFER_ASSETS_QUERY_STATUS:
                return TransferAssetsQueryStatus.apply();
            case SIMPLE_SCN:
                return SimpleScenario.apply();
        }
        return SimpleScenario.apply();
    }
}
