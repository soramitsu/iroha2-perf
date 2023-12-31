package jp.co.soramitsu.load.infrastructure.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum ScenarioEnum {
    IROHA_2_SET_UP("Iroha2SetUp"),
    TRANSFER_ASSETS_TRANSACTION_STATUS("TransferAssetsTransactionStatus"),
    TRANSFER_ASSETS_QUERY_STATUS("TransferAssetsQueryStatus"),
    SIMPLE_SCN("SimpleScenario");

    String value;

    public static ScenarioEnum findByValue(String value) {
        return Stream.of(ScenarioEnum.values())
            .filter(type -> type.getValue().equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow((() ->  new AssertionError(String.format("Can't find \"%s\" in ScenarioEnum", value))));
    }
}
