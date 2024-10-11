package scenarious.triggers;

import configs.tests.PalauPrecondition;
import io.gatling.javaapi.core.ScenarioBuilder;
import requests.Triggers;

import java.io.IOException;
import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;

public class RegisterSmartContracts {

    //TODO: create special precondition list
    // must be used onceOnly() controller
    public static ScenarioBuilder registerSmartContracts = scenario("register smart contracts")
            .exec(
                    Triggers.registerBondAssetTrigger,
                    Triggers.registerBuyBondsTrigger,
                    Triggers.registerRedeemBondsTrigger
            )
            .pace(Duration.ofSeconds(15));

    public static ScenarioBuilder registerBondAsset = scenario("register bond asset on each domain")
            .feed(csv("peers.csv").circular())
            .exec(session -> {
                        PalauPrecondition precondition;
                        try {
                            precondition = new PalauPrecondition();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        //сделай бля csv и не делай себе мозг![!!
                        session.set("assetDefinitionIdList", precondition.assetDefinitionIds);
                        session.set("domainIds", precondition.domainIds);
                        session.set("accountIds", precondition.accountIds);
                        return session;
                    }
            )
            .repeat(session -> {
                return session.getList("accountIds").size();
            })
            .on(exec(
                        Triggers.registerBondAsset
                    )
            );
}
