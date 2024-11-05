## Purpose
The genesis generator in the util module is configured to create specific preconditions necessary for load testing and testing of smart contracts.\
It does not retain standard accounts like Alice and Bob or default domains. These configurations require precise settings in a [config.properties](src/main/resources/config.properties) file and the creation of output directories.

To generate the required files:

Create a new directory named after the target branch and commit under:

```bash
performance-generator/src/test/resources/iroha2_config
```

Modify the [config.properties](src/main/resources/config.properties) file in the util module to specify paths and generation parameters.

#### Configuration Properties in config.properties

#### General Properties

* `incomeGenesis`: Directory where the original genesis.json from the Iroha project is located.\
* `outgoingGenesis`: Path where the generated genesis.json will be saved.\
* `outputCSV`: Directory where ${File}.csv with data for most scenarios will be saved.\
* `numberOfDomains`: Number of domains to create in genesis.json.\
* `numberOfAccounts`: Number of accounts per domain.\

#### **Note: These settings are specifically required for smart contract testing.**

#### Smart Contract

For smart contract tests, additional parameters should be configured:

* `generatedGenesis`: Path to either a previously generated genesis.json or the original genesis.json from the Iroha project.
* `userOnDomain`: Number of custom domains to register bonds in, excluding default domains (e.g., wonderland and garden_of_life_flowers).
* `bondCount`: Number of bonds in each domain.
* `assetDefinitionCountRelatedWithBond`: Number of assetDefinitions linked to bonds.
* `outgoingCSV`: Directory where generated files with preconditions will be saved.

#### Output Files

Upon execution, the generator will create the following CSV files in the specified output directory: 

* `domainIds.csv`: Lists the domains where bonds will be registered.
* `accountIds.csv`: Contains account owners of assetDefinitions.
* `assetDefinitionIds.csv`: Holds the assetDefinitions themselves.\

These files will be used in testing, specifically in: `performance-generator/src/test/java/simulation/smartcontracts`