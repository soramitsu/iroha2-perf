package scenarios.triggers;

import io.gatling.javaapi.core.ScenarioBuilder;

import requests.Triggers;
import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.scenario;

public class RegisterSmartContracts {

    // must be used onceOnly() controller
    public static ScenarioBuilder registerSmartContracts = scenario("register smart contracts")
            .exec(
                    Triggers.registerBondAssetTrigger,
                    Triggers.registerBuyBondsTrigger,
                    Triggers.registerRedeemBondsTrigger
            )
            .pace(Duration.ofSeconds(15));

    // must be used onceOnly() controller
    public static ScenarioBuilder registerBondAsset = scenario("register bond asset on each domain")
            .exec(
                    Triggers.registerBondAsset
            );
}
