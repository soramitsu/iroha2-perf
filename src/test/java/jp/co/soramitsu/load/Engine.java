package jp.co.soramitsu.load;

import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;
import jp.co.soramitsu.load.toolbox.BlueElectricalTape;

public class  Engine {

    public static void main(String[] args) {

        new BlueElectricalTape();

        GatlingPropertiesBuilder props = new GatlingPropertiesBuilder()
            .resourcesDirectory(IDEPathHelper.mavenResourcesDirectory.toString())
            .resultsDirectory(IDEPathHelper.resultsDirectory.toString())
            .binariesDirectory(IDEPathHelper.mavenBinariesDirectory.toString())
            .simulationClass("jp.co.soramitsu.load.simulation.LoadSimulation");
        Gatling.fromMap(props.build());


    }
}
