package configs.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class JsonParser {

    private JsonNode transactionNode;

    protected ArrayList<String> domainIds;

    protected ArrayList<String> accountIds;

    public JsonParser(String pathToGenesis) throws IOException {
        this.transactionNode = getTransactions(pathToGenesis);
        this.domainIds = getDomainIds(transactionNode);
        this.accountIds = getAccountIds(transactionNode, domainIds);
    }

    private JsonNode getTransactions(String pathToGenesis) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(JsonParser.class.getClassLoader().getResource(pathToGenesis).getFile());
        JsonNode rootNode = objectMapper.readTree(file);
        return rootNode.path("transactions");
    }

    private ArrayList<String> getDomainIds(JsonNode transactionNode) {
        for (JsonNode transactionArray : transactionNode) {
            for (JsonNode transaction : transactionArray) {
                JsonNode newDomainNode = transaction.path("Register").path("NewDomain");
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

    private ArrayList<String> getAccountIds(JsonNode transactionNode, ArrayList<String> domainIds) {
        int count = 0;

        for (JsonNode transactionArray : transactionNode) {
            for (int i = 0; i < domainIds.size(); i++) {
                for (JsonNode transaction : transactionArray) {
                    JsonNode newAccountNode = transaction.path("Register").path("NewAccount");
                    if (!newAccountNode.isMissingNode()) {
                        String domain = newAccountNode.path("id").asText().split("@")[1];
                        if (domain.equals(domainIds.get(i)) && count < 5) {
                            accountIds.add(newAccountNode.path("id").asText());
                            count++;
                        }
                        if (count == 5) {
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
