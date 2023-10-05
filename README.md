# iroha2-perf
Designing a Better World Through Decentralized Technologies

Edit configuration for local
build and run -> jp.co.soramitsu.load.Engine

# Debug simulation
mvn gatling:test -D gatling.simulationClass=jp.co.soramitsu.load.simulation.LoadSimulation \
-DtargetURL=${targetURL} \
-DremoteLogin=${remoteLogin} \
-DremotePass=${remotePass}   \
-Dintensity=(num)intensity(ISimulation)  \
-DstagesNumber=${params.stagesNumber}    \
-DstageDuration=${params.stageDuration}  \
-DrampDuration=${params.rampDuration}    \
-Dscenario=${params.scenario}

# MaximumSearchSimulation
mvn gatling:test -D gatling.simulationClass=jp.co.soramitsu.load.simulation.LoadSimulation \
-DtargetProtocol=${protocol} \
-DtargetURL=${url} \
-DtargetPath=${path} \
-DremoteLogin=${login} \
-DremotePass=${pass} \
-DdomainSetUpRumpUp=5 \
-DduringSetUp=4 \
-DsetUpUsersOnEachDomain=4 \
-DmaximumSearchRumpUp=1000 \
-DduringTest=360

# LoadSimulation
mvn gatling:test -D gatling.simulationClass=jp.co.soramitsu.load.simulation.LoadSimulation \
-DtargetProtocol=${protocol} \
-DtargetURL=${url} \
-DtargetPath=${path} \
-DremoteLogin=${login} \
-DremotePass=${pass} \
-DdomainSetUpRumpUp=5 \
-DduringSetUp=4 \
-DsetUpUsersOnEachDomain=4 \
-DrampUpTest=20 \
-DduringTest=10 \
-DstepDurationTest=1200
