# iroha2-perf
Designing a Better World Through Decentralized Technologies

Edit configuration for local
build and run -> jp.co.soramitsu.load.Engine
params -> gatling:test -D gatling.simulationClass=jp.co.soramitsu.load.simulation.LoadSimulation -DrampUp=10 -Dduring=20 -DstepDuration=60

mvn gatling:test -Dgatling.simulationClass=jp.co.soramitsu.load.simulation.LoadSimulation -DusersSetUp=2 -DrampUp=1 -DstepDuration=60

# Debug simulation
mvn gatling:test -Dgatling.simulationClass=jp.co.soramitsu.load.simulation.LoadSimulation -DusersSetUp=2 -DrampUp=1 -Dduring=1 -DstepDuration=60  -DattemptsToTransaction=10 -DattemptsToTransferTransaction=2

mvn gatling:test -D gatling.simulationClass=jp.co.soramitsu.load.simulation.LoadSimulation -Dintensity=(num)intensity(ISimulation)
-DstagesNumber=${params.stagesNumber} -DstageDuration=${params.stageDuration} -DrampDuration=${params.rampDuration}
-Dscenario=${params.scenario}
