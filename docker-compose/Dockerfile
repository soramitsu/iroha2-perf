FROM ubuntu:latest

RUN apt-get update && apt-get install -y wget unzip openjdk-11-jdk maven
RUN apt-get install -y wget && \
    wget https://github.com/JetBrains/kotlin/releases/download/v1.8.0/kotlin-compiler-1.8.0.zip && \
    unzip kotlin-compiler-1.8.0.zip -d /opt && \
    rm kotlin-compiler-1.8.0.zip

WORKDIR /app
COPY . /app
RUN mvn clean install

CMD ["mvn", "gatling:test", "-Dgatling.simulationClass=$SIMULATION_CLASS", "-DtargetProtocol=$TARGET_PROTOCOL", "-DtargetURL=$TARGET_URL", "-DtargetPath=$TARGET_PATH", "-DremoteLogin=$REMOTE_LOGIN", "-DremotePass=$REMOTE_PASS", "-DconcurrentUsers=$CONCURRENT_USERS", "-Dtimes=$TIMES", "-DeachLevelLasting=$EACH_LEVEL_LASTING", "-DseparatedByRampsLasting=$SEPARATED_BY_RAMPS_LASTING", "-DstartingFrom=$STARTING_FROM", "-DdomainSetUpRampUp=$DOMAIN_SETUP_RAMP_UP", "-DduringSetUp=$DURING_SET_UP", "-DsetUpUsersOnEachDomain=$SET_UP_USERS_ON_EACH_DOMAIN"]