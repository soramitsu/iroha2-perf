package jp.co.soramitsu.load;

import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig;

public class Engine {
    public static void main(String[] args) {
        GatlingPropertiesBuilder props = new GatlingPropertiesBuilder()
                .resourcesDirectory(IDEPathHelper.mavenResourcesDirectory.toString())
                .resultsDirectory(IDEPathHelper.resultsDirectory.toString())
                .binariesDirectory(IDEPathHelper.mavenBinariesDirectory.toString())
                //.simulationClass(SimulationConfig.simulation.loadSimulation());
                .simulationClass(SimulationConfig.simulation.maximumSearchSimulation());
        Gatling.fromMap(props.build());
    }
}
