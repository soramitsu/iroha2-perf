package jp.co.soramitsu.load.infrastructure.config;

import com.redis.S;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;
import org.checkerframework.checker.units.qual.K;

@LoadPolicy(LoadType.MERGE)
@Sources({
    "system:properties",
    "system:env",
    "classpath:simulation.properties"
})
public interface Simulation extends Config {
    //SetUp model settings
    @Key("targetProtocol")
    String targetProtocol();
    @Key("targetURL")
    String targetURL();
    @Key("remoteLogin")
    String remoteLogin();
    @Key("remotePass")
    String remotePass();
    @Key("logLevel")
    String logLevel();
    @Key("configuration")
    String configuration();

    //Close model settings
    @Key("concurrentUsers")
    Integer concurrentUsers();
    @Key("times")
    Integer times();
    @Key("eachLevelLasting")
    Long eachLevelLasting();
    @Key("separatedByRampsLasting")
    Long separatedByRampsLasting();
    @Key("startingFrom")
    Integer startingFrom();
    @Key("loadSimulationTrigger")
    String loadSimulationTrigger();
    @Key("loadSimulation")
    String loadSimulation();
    @Key("maximumSearchSimulation")
    String maximumSearchSimulation();
    @Key("stressSimulation")
    String stressSimulation();

    //SetUp section
    @Key("domainSetUpRumpUp")
    int domainSetUpRumpUp();

    //Load model
    @Key("intensity")
    int intensity();
    @Key("rampDuration")
    int rampDuration();
    @Key("stageDuration")
    int stageDuration();

    //Stress model
    @Key("stressIntensity")
    int stressIntensity();
    @Key("stressRampDuration")
    int stressRampDuration();
    @Key("stressDuration")
    int stressDuration();

    //TransferAssetsQueryStatus
    @Key("attemptsToTransaction")
    int getAttemptsToTransaction();

    @Key("attemptsToTransferTransaction")
    int getAttemptsToTransferTransaction();

    @Key("scenario")
    String getScn();

    @Key("setUpUsersOnEachDomain")
    int getSetUpUsersOnEachDomain();
}
