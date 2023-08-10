# iroha2-perf
Designing a Better World Through Decentralized Technologies

Edit configuration for local
build and run -> jp.co.soramitsu.load.Engine
params -> gatling:test -D gatling.simulationClass=jp.co.soramitsu.load.simulation.DebugSimulation -Dscenario=FindAllDomain 

mvn gatling:test -D gatling.simulationClass=jp.co.soramitsu.load.simulation.${params.simulationClass} -Dintensity=(num)intensity(ISimulation)
-DstagesNumber=${params.stagesNumber} -DstageDuration=${params.stageDuration} -DrampDuration=${params.rampDuration} 
-Dscenario=${params.scenario}
