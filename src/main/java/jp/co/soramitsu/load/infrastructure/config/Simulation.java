package jp.co.soramitsu.load.infrastructure.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;

@LoadPolicy(LoadType.MERGE)
@Sources({
    "system:properties",
    "system:env",
    "classpath:simulation.properties"
})
public interface Simulation extends Config {

    @Key("rampDuration")
    int rampDuration();

    @Key("scenario")
    String getScn();

    @Key("scenario0")
    String getScn0();

    @Key("usersForSetUp")
    int getUsersForSetUp();

    @Key("usersSetUp")
    int getUsersSetUp();

    @Key("attemptsToTransaction")
    int getAttemptsToTransaction();

    @Key("attemptsToTransferTransaction")
    int getAttemptsToTransferTransaction();

    @Key("rampUp")
    int rampUp();

    @Key("during")
    int during();

    @Key("stepDuration")
    int stepDuration();

    @Key("maximumSearchRumpUp")
    int maximumSearchRumpUp();

    @Key("stressPeakRampUp")
    int stressPeakRampUp();

    @Key("stressPeakDuration")
    int stressPeakDuration();
}
