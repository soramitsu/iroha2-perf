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

# Image publishing policy
We use a composite tag naming for `iroha2-perf` image. It's constructed as:
`iroha2-perf:<semver-version>-<iroha2-release>`
Bump tag with minor version unless the merge commit message contains #major or #patch.
For more information, take a look to the [reference](https://github.com/anothrNick/github-tag-action#bumping)
Chanhe the iroha2 release value for image tag by running workflow within `dispatch` event and set the value in `GITHUB_IROHA2_BRANCH` variable.
