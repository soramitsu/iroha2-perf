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

    //SetUp section
    @Key("domainSetUpRumpUp")
    int domainSetUpRumpUp();

    @Key("duringSetUp")
    int duringSetUp();

    //Load model
    @Key("rampUpTest")
    int rampUpTest();

    @Key("duringTest")
    int duringTest();

    @Key("stepDurationTest")
    int stepDurationTest();

    //TransferAssetsQueryStatus
    @Key("attemptsToTransaction")
    int getAttemptsToTransaction();

    @Key("attemptsToTransferTransaction")
    int getAttemptsToTransferTransaction();

    @Key("scenario")
    String getScn();

    @Key("scenario0")
    String getScn0();

    @Key("setUpUsersOnEachDomain")
    int getSetUpUsersOnEachDomain();

    @Key("maximumSearchRumpUp")
    int maximumSearchRumpUp();

    @Key("stressPeakRampUp")
    int stressPeakRampUp();

    @Key("stressPeakDuration")
    int stressPeakDuration();
}
