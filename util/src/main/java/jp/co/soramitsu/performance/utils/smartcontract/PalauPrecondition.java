package jp.co.soramitsu.performance.utils.smartcontract;

import com.opencsv.CSVWriter;
import jp.co.soramitsu.performance.utils.ConfigLoader;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class PalauPrecondition {

    public static ArrayList<String> assetDefinitionIds;

    public static ArrayList<String> domainIds;

    public static ArrayList<String> accountIds;

    private ConfigLoader config;

    private final String accountOutputCSV;

    private final String domainsOutputCSV;

    private final String assetDefinitionOutputCSV;

    private final BufferedWriter accountCsvWriter;

    private final CSVWriter accountWriter;

    private final BufferedWriter domainCsvWriter;

    private final CSVWriter domainWriter;

    private final BufferedWriter assetDefinitionCsvWriter;

    private final CSVWriter assetDefinitionWriter;

    private final String[] accountIdHeader = {
            "accountIdForTrigger"
    };

    private final String[] headerDomainId = {
            "domainIdForTrigger"
    };

    private final String[] headerAssetDefinitionId = {
            "assetDefinitionIdForTrigger"
    };

    public static void main(String[] args) throws IOException {
        PalauPrecondition palauPrecondition = new PalauPrecondition();
    }

    public PalauPrecondition() throws IOException {
        this.config = new ConfigLoader();
        this.accountOutputCSV = config.getProperty("outgoingCSV") + "accountIds.csv";
        this.domainsOutputCSV = config.getProperty("outgoingCSV") + "domainIds.csv";
        this.assetDefinitionOutputCSV = config.getProperty("outgoingCSV") + "assetDefinitionIds.csv";
        this.accountCsvWriter = new BufferedWriter(new FileWriter(accountOutputCSV));
        this.accountWriter = new CSVWriter(accountCsvWriter, '\n', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, "\n");
        this.domainCsvWriter = new BufferedWriter(new FileWriter(domainsOutputCSV));
        this.domainWriter = new CSVWriter(domainCsvWriter, '\n', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, "\n");
        this.assetDefinitionCsvWriter = new BufferedWriter(new FileWriter(assetDefinitionOutputCSV));
        this.assetDefinitionWriter = new CSVWriter(assetDefinitionCsvWriter, '\n', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, "\n");

        JsonParser parser = new JsonParser(
                config.getProperty("generatedGenesis"),
                Integer.parseInt(config.getProperty("userOnDomain"))
        );

        AssetDefinitionIdGenerator assetDefinitionIdGenerator = new AssetDefinitionIdGenerator(
                Integer.parseInt(config.getProperty("bondCount")),
                Integer.parseInt(config.getProperty("userOnDomain"))
        );

        this.assetDefinitionIds = assetDefinitionIdGenerator.listAssetDefinitionId;
        this.domainIds = parser.domainIds;
        this.accountIds = parser.accountIds;

        scvGenerator(accountWriter, accountIdHeader, accountIds);
        csvWriterClose(accountWriter);

        scvGenerator(domainWriter, headerDomainId, domainIds);
        csvWriterClose(domainWriter);

        scvGenerator(assetDefinitionWriter, headerAssetDefinitionId, assetDefinitionIds);
        csvWriterClose(assetDefinitionWriter);
    }

    private void csvWriter(@NotNull CSVWriter writer, String[] any) {
        writer.writeNext(any);
    }

    private void csvWriterClose(@NotNull CSVWriter writer) throws IOException {
        writer.close();
    }

    private void scvGenerator(CSVWriter writer, String[] header, @NotNull ArrayList<String> list) {
        String[] arr = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }

        csvWriter(writer, header);
        csvWriter(writer, arr);
    }

}
