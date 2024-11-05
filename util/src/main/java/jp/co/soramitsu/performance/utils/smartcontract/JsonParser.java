package jp.co.soramitsu.performance.utils.smartcontract;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.soramitsu.performance.utils.ConfigLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class JsonParser {

    protected ArrayList<String> domainIds = new ArrayList<>();
    protected ArrayList<String> accountIds = new ArrayList<>();
    private JsonNode transactionNode;
    private ConfigLoader config;

    public JsonParser(String pathToGenesis, int usersOnDomain) throws IOException {
        this.config = new ConfigLoader();
        this.transactionNode = getTransactions(pathToGenesis);
        this.domainIds = getDomainIds(transactionNode);
        this.accountIds = getAccountIds(transactionNode, domainIds, usersOnDomain);
    }

    private JsonNode getTransactions(String pathToGenesis) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        //File file = new File(JsonParser.class.getClassLoader().getResource(config.getProperty("generatedGenesis")).getFile());
        File file = new File(config.getProperty("generatedGenesis"));
        JsonNode rootNode = objectMapper.readTree(file);
        return rootNode.path("instructions");
    }

    private ArrayList<String> getDomainIds(JsonNode transactionNode) {
        for (JsonNode transactionArray : transactionNode) {
            for (JsonNode transaction : transactionArray) {
                JsonNode newDomainNode = transaction.path("Domain");
                if (!newDomainNode.isMissingNode()) {
                    String domain = newDomainNode.path("id").asText();
                    if (!domain.equals("wonderland") && !domain.equals("garden_of_live_flowers")) {
                        domainIds.add(domain);
                    }
                }
            }
        }
        return domainIds;
    }

    private ArrayList<String> getAccountIds(JsonNode transactionNode, ArrayList<String> domainIds, int usersOnDomain) {
        int count = 0;

        for (JsonNode transactionArray : transactionNode) {
            for (int i = 0; i < domainIds.size(); i++) {
                for (JsonNode transaction : transactionArray) {
                    JsonNode newAccountNode = transaction.path("Account");
                    if (!newAccountNode.isMissingNode()) {
                        String domain = newAccountNode.path("id").asText().split("@")[1];
                        if (domain.equals(domainIds.get(i)) && count < usersOnDomain) {
                            accountIds.add(newAccountNode.path("id").asText());
                            count++;
                        }
                        if (count == Integer.parseInt(config.getProperty("assetDefinitionCountRelatedWithBond"))) {
                            if (!domain.equals(domainIds.get(i))) {
                                count = 0;
                            }
                        }

                    }
                }
            }
        }
        return accountIds;
    }
}
