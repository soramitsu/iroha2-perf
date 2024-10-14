package configs.tests;

import com.opencsv.CSVWriter;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class PalauPrecondition  {

    public static ArrayList<String> assetDefinitionIds;

    public static ArrayList<String> domainIds;

    public static ArrayList<String> accountIds;

    private final String path = "/Users/michaeltimofeev/Documents/projects/iroha2-perf/src/test/resources/iroha2_config/stable/5d44d59/";

    private final String accountOutputCSV = path + "accountIds.csv";

    private final String domainsOutputCSV = path + "domainIds.csv";

    private final String assetDefinitionOutputCSV = path + "assetDefinitionIds.csv";

    private final BufferedWriter accountCsvWriter = new BufferedWriter(new FileWriter(accountOutputCSV));

    private final BufferedWriter domainCsvWriter = new BufferedWriter(new FileWriter(domainsOutputCSV));

    private final BufferedWriter assetDefinitionCsvWriter = new BufferedWriter(new FileWriter(assetDefinitionOutputCSV));

    private final CSVWriter accountWriter = new CSVWriter(accountCsvWriter, '\n', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, "\n");

    private final CSVWriter domainWriter = new CSVWriter(domainCsvWriter, '\n', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, "\n");

    private final CSVWriter assetDefinitionWriter = new CSVWriter(assetDefinitionCsvWriter, '\n', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, "\n");

    private final String[] accountIdHeader = {
            "accountIdForTrigger"
    };

    private final String[] headerDomainId = {
            "domainIdForTrigger"
    };

    private final String[] headerAssetDefinitionId = {
            "assetDefinitionIdForTrigger"
    };

    public PalauPrecondition() throws IOException {
        JsonParser parser = new JsonParser(path + "genesis.json");
        AssetDefinitionIdGenerator assetDefinitionIdGenerator = new AssetDefinitionIdGenerator(15);

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

    public static void main(String[] args) throws IOException {
        PalauPrecondition palauPrecondition = new PalauPrecondition();
    }

}
