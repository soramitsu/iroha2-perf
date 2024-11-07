### Iroha2-performance

If you need help and my manual didn’t assist you, you can reach out to

**Michael Timofeev - Performance engineer**\
**Ramil Mustafin - DevOps engineer**

But let’s respect each other's work time. Please read all the README.md files carefully. I’ve tried to describe everything as thoroughly as possible.

This project is designed to perform load testing using Gatling and includes utilities for generating essential test data.
Java 17, Maven, and the Maven Wrapper for consistent environment setup and execution.

The project is divided into two modules:
1. performance-generator: Houses load testing scenarios and simulations using Gatling.
2. util: Contains data generation tools for creating preconditions such as genesis.json and formatted lists for testing smart contracts. 


## Table of Contents
- [Getting Started](#getting-started)
- [Modules](#modules)
  - [Performance-Generator](#performance-generator)
  - [Util](./util/README.md)
- [Running Load Tests](#running-load-tests)
  1. [Standard Load Model](#standard-load-model)
  2. [Gradual Load Model](#gradual-load-model)
- [Data Generators in util](#data-enerators-in-til)
- [Feeder Files](#feeder-files)


## Getting Started

#### Requirements
* Java 17 or higher
* Maven 3.6+
* Maven Wrapper

## Modules
#### 1. performance-generator
The performance-generator module contains load testing scenarios and simulations written using Gatling.

**Simulation Execution**: Defines load testing strategies.\
**Scenario Management**: Manages and structures requests, intensity, and duration of load.

**Versioning of Configuration Files in iroha2_config**

The `iroha2_config` directory contains all the necessary configuration files for deploying Iroha2 on the longevity testing environment, including:

`executor.wasm`\
`genesis.json`\
`docker-compose.yml`\
`client.toml`

Additionally, it includes corresponding **files with preconditions for load testing**.

For Transaction and Query Load Tests:
* pronditionist.csv
* preconditionListMultiTxs.csv

For Smart Contract Load Tests:
* accountIds.csv
* assetDefinitionIds.csv
* bondIds.csv
* domainIds.csv

The configuration files are located at: `iroha2_config/${Path_To_Iroha_Commit_Number}/docker-compose/`

The smart contracts are located at: `iroha2_config/${Path_To_Iroha_Commit_Number}/smartContracts/`


#### 2. util
The util module provides data generation utilities for creating necessary prerequisites for load tests, including:

**Genesis**: Generates genesis.json.\
Formatted **Precondition Lists**: Prepares and formats preconditions for smart contract testing.\
This module allows for flexible data generation, making it easy to configure scenarios for different load testing requirements.

## Running Load Tests
The project uses Gatling for load testing. The following sections explain how to run different load models and configure parameters for a target environment.
For further parameter options and configuration, refer to the [Gatling Load Models documentation](https://docs.gatling.io/reference/script/core/injection/#open-vs-closed-workload-models).

#### 1. Standard Load Model
   The Standard Load Model is ideal for controlled load testing, useful for both stability and peak testing.

``` bash
./mvnw gatling:test
-Dgatling.simulationClass=${Path_To_Simulation_Class}
-DtargetURL=${Target_Host_URL}
-DremoteLogin=${Remote_Login}
-DremotePassword=${Remote_Password}
-Dintensity=${Users_Per_Ramp_Duration}
-DrampDuration=${Ramp_Duration_Seconds}
-DmaxDuration=${Max_Test_Duration_Seconds}
```

Parameter Explanation:

* `gatling.simulationClass`: The simulation class to run. This should correspond to a class under simulation.
* `targetURL`: The URL of the target system under test.
* `remoteLogin`: Remote login username for authentication.
* `remotePassword`: Password for the remote login.
* `intensity`: Number of users to be introduced over each rampDuration.
* `rampDuration`: Time (in seconds) over which the intensity users will be introduced.
* `maxDuration`: Maximum duration (in seconds) for the test. The test will terminate after this duration, regardless of outstanding requests.


#### 2. Gradual Load Model

The Gradual Load Model is optimal for determining the maximum performance thresholds of the system under test.
``` bash
./mvnw gatling:test
-Dgatling.simulationClass=${Path_To_Simulation_Class}
-DtargetURL=${Target_Host_URL}
-DremoteLogin=${Remote_Login}
-DremotePassword=${Remote_Password}
-DconcurrentUsers=${Concurrent_Users}
-Dtimes=${Times}
-DstageDuration=${Stage_Duration}
-DstartingFrom=${Starting_From}
-DmaxDuration=${Max_Duration}
```

Parameter Explanation:

* `concurrentUsers`: Number of users added each second, increasing the load incrementally.
* `times`: Number of times each level of concurrentUsers will be repeated.
* `stageDuration` (eachLevelLasting): Duration (in seconds) for each stage of user increments.
* `startingFrom`: Initial number of users to start with.
* `maxDuration`: Maximum test duration (in seconds); the test will terminate once this limit is reached.


## Data Generators in util
The util module includes tools to generate data files required for load tests.
The generated files are stored in directories named based on the Iroha branch and commit, allowing easy association with specific versions.

* **Genesis Configuration (genesis.json)**: Defines initial configuration and state for testing environments.
* **Precondition Lists**: Prepares data, such as accounts and assets, formatted specifically for smart contract testing.

#### Feeder Files
[CSV-Generator](util/README.md)

Depending on the test scenario, `${Any_Feeder}.csv` files may contain different fields. Feeder configurations are scenario-specific, allowing for flexible data injection into test requests.

Example Feeder Files: Sample feeder files and their configurations can be found under `performance-generator/src/test/java/requests`.
For details on how feeders work and best practices for using them in Gatling, refer to the official [Gatling Feeder documentation](https://docs.gatling.io/reference/script/core/session/feeders/).


