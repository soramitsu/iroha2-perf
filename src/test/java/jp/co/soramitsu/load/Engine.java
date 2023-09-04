package jp.co.soramitsu.load;

import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;
import jp.co.soramitsu.load.infrastructure.config.SimulationConfig;
import jp.co.soramitsu.load.objects.CustomHistogram;
import jp.co.soramitsu.load.toolbox.BlueElectricalTape;
import jp.co.soramitsu.load.toolbox.Grinder;

import java.net.MalformedURLException;

public class  Engine {
    public static void main(String[] args) {
            GatlingPropertiesBuilder props = new GatlingPropertiesBuilder()
                    .resourcesDirectory(IDEPathHelper.mavenResourcesDirectory.toString())
                    .resultsDirectory(IDEPathHelper.resultsDirectory.toString())
                    .binariesDirectory(IDEPathHelper.mavenBinariesDirectory.toString())
                    .simulationClass(SimulationConfig.simulation.debugSimulation());
            Gatling.fromMap(props.build());
    }
}
