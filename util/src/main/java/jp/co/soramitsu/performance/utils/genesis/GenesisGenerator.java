package jp.co.soramitsu.performance.utils.genesis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jp.co.soramitsu.performance.utils.Constants;
import jp.co.soramitsu.performance.models.Account;
import jp.co.soramitsu.performance.utils.ConfigLoader;
import org.jetbrains.annotations.NotNull;

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

        addParametrs(rootNode);
        addDomain(instructionsNode, Credentials.WONDERLAND.getValue());
        addAccount(instructionsNode,Credentials.ALICE.getValue() + "@" + Credentials.WONDERLAND.getValue());
        addAccount(instructionsNode,Credentials.BOB.getValue() + "@" + Credentials.WONDERLAND.getValue());
        addAssetDefinition(instructionsNode, Credentials.ASSET_DEFINITION_ROSE.getValue() + "#" + Credentials.WONDERLAND.getValue());
        addDomain(instructionsNode, Credentials.GARDEN_OF_LIVE_FLOWERS.getValue());
        addAccount(instructionsNode,Credentials.CARPENTER.getValue() + "@" + Credentials.GARDEN_OF_LIVE_FLOWERS.getValue());
        addAssetDefinition(instructionsNode, Credentials.ASSET_DEFINITION_CABBAGE.getValue() + "#" + Credentials.GARDEN_OF_LIVE_FLOWERS.getValue());
        addAsset(instructionsNode, Credentials.ASSET_DEFINITION_ROSE.getValue() + "##" + Credentials.ALICE.getValue() + "@" + Credentials.WONDERLAND.getValue());
        addAsset(instructionsNode, Credentials.ASSET_DEFINITION_CABBAGE.getValue() + "#" + Credentials.GARDEN_OF_LIVE_FLOWERS.getValue() + "#" + Credentials.ALICE.getValue() + "@" + Credentials.WONDERLAND.getValue());


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

        addTransferInstruction(instructionsNode,
                "AssetDefinition",
                "ed01204164BF554923ECE1FD412D241036D863A6AE430476C898248B8237D77534CFC4@genesis",
                Credentials.ASSET_DEFINITION_ROSE.getValue() + "#" + Credentials.WONDERLAND.getValue(),
                Credentials.ALICE.getValue() + "@" + Credentials.WONDERLAND.getValue()
        );
        addTransferInstruction(instructionsNode,
                "Domain",
                "ed01204164BF554923ECE1FD412D241036D863A6AE430476C898248B8237D77534CFC4@genesis",
                Credentials.WONDERLAND.getValue(),
                Credentials.ALICE.getValue() + "@" + Credentials.WONDERLAND.getValue()
        );
        addPermission(instructionsNode,
                Credentials.CAN_SET_PARAMETERS.getValue(),
                Credentials.ALICE.getValue() + "@" + Credentials.WONDERLAND.getValue()
        );
        addPermission(instructionsNode,
                Credentials.CAN_REGISTER_DOMAIN.getValue(),
                Credentials.ALICE.getValue() + "@" + Credentials.WONDERLAND.getValue()
        );

        addTopology(rootNode);
        objectMapper.writeValue(outgoingGenesis, rootNode);
    }

    private static void addPermission(@NotNull ArrayNode instructionNode, @NotNull String name, @NotNull String destination) {
        ObjectNode grantNode = instructionNode.addObject();
        ObjectNode grantObject = grantNode.putObject("Grant");
        ObjectNode permissionNode = grantObject.putObject("Permission");
        ObjectNode objectNode = permissionNode.putObject("object");
        objectNode.put("name", name);
        objectNode.putNull("payload");
        permissionNode.put("destination", destination);
    }

    private static void addTransferInstruction(@NotNull ArrayNode instructionsNode, @NotNull String item, @NotNull String source, @NotNull String object, @NotNull String destination) {
        ObjectNode transferNode = instructionsNode.addObject();
        ObjectNode registerNode = transferNode.putObject("Transfer");
        ObjectNode assetDefinitionNode;

        if(item.equals("AssetDefinition")){
            assetDefinitionNode = registerNode.putObject("AssetDefinition");
        } else {
            assetDefinitionNode = registerNode.putObject("Domain");
        }

        assetDefinitionNode.put("source", source);
        assetDefinitionNode.put("object", object);
        assetDefinitionNode.put("destination", destination);
    }

    private static void addTopology(@NotNull ObjectNode rootNode) {
        rootNode.putArray("topology");
    }

    private static void addParametrs(@NotNull ObjectNode rootNode) {
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

    private static void addDomain(@NotNull ArrayNode instructionsNode, String domainId) {
        ObjectNode domainNode = instructionsNode.addObject();
        ObjectNode registerNode = domainNode.putObject("Register");
        ObjectNode domainObject = registerNode.putObject("Domain");
        domainObject.put("id", domainId);
        domainObject.putNull("logo");
        ObjectNode metadataNode = domainObject.putObject("metadata");
        metadataNode.put("key", "value");
    }

    private static void addAccount(@NotNull ArrayNode instructionNode, @NotNull Account account) {
        ObjectNode accountNode = instructionNode.addObject();
        ObjectNode registerNode = accountNode.putObject("Register");
        ObjectNode accountObject = registerNode.putObject("Account");
        accountObject.put("id", account.getGenesisPublicKey() + "@" + account.getDomainId());
        ObjectNode metadataNode = accountObject.putObject("metadata");
        metadataNode.put("key", "value");
    }

    private static void addAccount(@NotNull ArrayNode instructionNode, String account) {
        ObjectNode accountNode = instructionNode.addObject();
        ObjectNode registerNode = accountNode.putObject("Register");
        ObjectNode accountObject = registerNode.putObject("Account");
        accountObject.put("id", account);
        ObjectNode metadataNode = accountObject.putObject("metadata");
        metadataNode.put("key", "value");
    }

    private static void addAssetDefinition(@NotNull ArrayNode instructionsNode, @NotNull Account account) {
        ObjectNode assetDefinitionNode = instructionsNode.addObject();
        ObjectNode registerNode = assetDefinitionNode.putObject("Register");
        ObjectNode assetDefinitionObject = registerNode.putObject("AssetDefinition");
        assetDefinitionObject.put("id", account.getAssetDefinition().toString());
        assetDefinitionObject.put("type", "Numeric");
        assetDefinitionObject.put("mintable", "Infinitely");
        assetDefinitionObject.putNull("logo");
        ObjectNode metadataNode = assetDefinitionObject.putObject("metadata");
    }

    private static void addAssetDefinition(@NotNull ArrayNode instructionsNode, @NotNull String assetDefinitionId) {
        ObjectNode assetDefinitionNode = instructionsNode.addObject();
        ObjectNode registerNode = assetDefinitionNode.putObject("Register");
        ObjectNode assetDefinitionObject = registerNode.putObject("AssetDefinition");
        assetDefinitionObject.put("id", assetDefinitionId);
        assetDefinitionObject.put("type", "Numeric");
        assetDefinitionObject.put("mintable", "Infinitely");
        assetDefinitionObject.putNull("logo");
        ObjectNode metadataNode = assetDefinitionObject.putObject("metadata");
    }

    private static void addAsset(@NotNull ArrayNode instructionsNode, @NotNull Account account) {
        ObjectNode assetNode = instructionsNode.addObject();
        ObjectNode registerNode = assetNode.putObject("Mint");
        ObjectNode assetObject = registerNode.putObject("Asset");
        assetObject.put("object", "100000");
        assetObject.put("destination", account.getAsset());
    }

    private static void addAsset(@NotNull ArrayNode instructionsNode, @NotNull String assetId) {
        ObjectNode assetNode = instructionsNode.addObject();
        ObjectNode registerNode = assetNode.putObject("Mint");
        ObjectNode assetObject = registerNode.putObject("Asset");
        assetObject.put("object", "100000");
        assetObject.put("destination", assetId);
    }
}
