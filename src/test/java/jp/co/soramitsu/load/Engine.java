package jp.co.soramitsu.load;

import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;
import jp.co.soramitsu.load.toolbox.BlueElectricalTape;
import jp.co.soramitsu.load.toolbox.Grinder;

import java.net.MalformedURLException;

public class  Engine {
    public static void main(String[] args) throws MalformedURLException {
        try{
            new BlueElectricalTape();
            new Grinder();
            GatlingPropertiesBuilder props = new GatlingPropertiesBuilder()
                    .resourcesDirectory(IDEPathHelper.mavenResourcesDirectory.toString())
                    .resultsDirectory(IDEPathHelper.resultsDirectory.toString())
                    .binariesDirectory(IDEPathHelper.mavenBinariesDirectory.toString())
                    .simulationClass("jp.co.soramitsu.load.simulation.LoadSimulation");
            Gatling.fromMap(props.build());
        } finally {
            new Grinder();
        }
    }
}
