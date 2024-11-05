package jp.co.soramitsu.performance.utils.genesis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jp.co.soramitsu.performance.utils.Constants;
import jp.co.soramitsu.performance.models.Account;
import jp.co.soramitsu.performance.utils.ConfigLoader;

import java.io.File;
import java.io.IOException;

public class GenesisGenerator extends Constants {

    public GenesisGenerator() throws IOException {
    }

    public static void main(String[] args) throws IOException {
        ConfigLoader config = new ConfigLoader();
        ObjectMapper objectMapper = new ObjectMapper();
        File incomeGenesis = new File(config.getProperty("incomeGenesis"));
        File outgoingGenesis = new File(config.getProperty("outgoingGenesis"));
        ObjectNode rootNode = (ObjectNode) objectMapper.readTree(incomeGenesis);
        ArrayNode instructionsNode = (ArrayNode) rootNode.get("instructions");
        ArrayNode instructionsNode2 = (ArrayNode) rootNode.get("instructions");

        int numberOfDomains = Integer.parseInt(config.getProperty("numberOfDomains"));
        int numberOfAccounts = Integer.parseInt(config.getProperty("numberOfAccounts"));

        CsvGenerator csvGenerator = new CsvGenerator(numberOfDomains, numberOfAccounts);

        instructionsNode.removeAll();

        addFields(rootNode);

        for (int i = 0; i < domainIds.size(); i++) {
            addDomain(instructionsNode, domainIds.get(i).getId());
            for (int j = 0; j < accountIds.size(); j++) {
                if (accountIds.get(j).getId().split("@")[1].equals(domainIds.get(i).getId())) {
                    addAccount(instructionsNode, accountIds.get(j));
                    if (accountIds.get(j).getAssetDefinition() != null) {
                        addAssetDefinition(instructionsNode, accountIds.get(j));
                        addAsset(instructionsNode, accountIds.get(j));
                    }
                }
            }
        }
        addTopology(rootNode);
        objectMapper.writeValue(outgoingGenesis, rootNode);
    }

    private static void addTransferInstruction(ArrayNode instructionsNode, Account account) {
        ObjectNode transferNode = instructionsNode.addObject();
        ObjectNode registerNode = transferNode.putObject("Transfer");
        ObjectNode assetDefinitionNode = registerNode.putObject("AssetDefinition");

        assetDefinitionNode.put("source", account.getPublicKey() + "@genesis.json");
        assetDefinitionNode.put("object", account.getAssetDefinition().toString());
        assetDefinitionNode.put("destination", account.getId());
    }

    private static void addTopology(ObjectNode rootNode) {
        rootNode.putArray("topology");
    }

    private static void addFields(ObjectNode rootNode) {
        rootNode.put("chain", "00000000-0000-0000-0000-000000000000");
        rootNode.put("executor", "./executor.wasm");
        ObjectNode parametersNode = rootNode.putObject("parameters");

        ObjectNode sumeragiNode = parametersNode.putObject("sumeragi");
        sumeragiNode.put("block_time_ms", 500);
        sumeragiNode.put("commit_time_ms", 1000);
        sumeragiNode.put("max_clock_drift_ms", 1000);

        ObjectNode blockNode = parametersNode.putObject("block");
        blockNode.put("max_transactions", 600);

        ObjectNode transactionNode = parametersNode.putObject("transaction");
        transactionNode.put("max_instructions", 4096);
        transactionNode.put("smart_contract_size", 4194304);

        ObjectNode executorNode = parametersNode.putObject("executor");
        executorNode.put("fuel", 524288000000L);
        executorNode.put("memory", 4294967295L);

        ObjectNode smartContractNode = parametersNode.putObject("smart_contract");
        smartContractNode.put("fuel", 524288000000L);
        smartContractNode.put("memory", 4294967295L);
    }

    private static void addDomain(ArrayNode instructionsNode, String domainId) {
        ObjectNode domainNode = instructionsNode.addObject();
        ObjectNode registerNode = domainNode.putObject("Register");
        ObjectNode domainObject = registerNode.putObject("Domain");
        domainObject.put("id", domainId);
        domainObject.putNull("logo");
        ObjectNode metadataNode = domainObject.putObject("metadata");
        metadataNode.put("key", "value");
    }

    private static void addAccount(ArrayNode instructionNode, Account account) {
        ObjectNode accountNode = instructionNode.addObject();
        ObjectNode registerNode = accountNode.putObject("Register");
        ObjectNode accountObject = registerNode.putObject("Account");
        accountObject.put("id", account.getGenesisPublicKey() + "@" + account.getDomainId());
        ObjectNode metadataNode = accountObject.putObject("metadata");
        metadataNode.put("key", "value");
    }

    private static void addAssetDefinition(ArrayNode instructionsNode, Account account) {
        ObjectNode assetDefinitionNode = instructionsNode.addObject();
        ObjectNode registerNode = assetDefinitionNode.putObject("Register");
        ObjectNode assetDefinitionObject = registerNode.putObject("AssetDefinition");
        assetDefinitionObject.put("id", account.getAssetDefinition().toString());
        assetDefinitionObject.put("type", "Numeric");
        assetDefinitionObject.put("mintable", "Infinitely");
        assetDefinitionObject.putNull("logo");
        ObjectNode metadataNode = assetDefinitionObject.putObject("metadata");
    }

    private static void addAsset(ArrayNode instructionsNode, Account account) {
        ObjectNode assetNode = instructionsNode.addObject();
        ObjectNode registerNode = assetNode.putObject("Mint");
        ObjectNode assetObject = registerNode.putObject("Asset");
        assetObject.put("object", "100000");
        assetObject.put("destination", account.getAsset());
        //assetObject.put("destination", account.getAsset() + "##" + account.getGenesisPublicKey() + "@" + account.getDomainId());
    }
}
