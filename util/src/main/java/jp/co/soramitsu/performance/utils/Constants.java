package jp.co.soramitsu.performance.utils;

import com.opencsv.CSVWriter;
import jp.co.soramitsu.performance.models.Account;
import jp.co.soramitsu.performance.models.Domain;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Constants {

    private ConfigLoader config;

    protected static ArrayList<Domain> domainIds = new ArrayList();

    protected static ArrayList<Account> accountIds = new ArrayList();

    protected String outputCSV = config.getProperty("outputCSV");

    protected BufferedWriter csvWriter = new BufferedWriter(new FileWriter(outputCSV));

    protected CSVWriter writer = new CSVWriter(csvWriter, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, "\n");

    protected String[] standardUserSetHeaders = {
            "anotherDevAccountIdSender"
            , "publicKeySender"
            , "privateKeySender"
            , "domainIdSender"
            , "assetDefinitionIdSender"
            , "anotherDevAssetIdSender"
            , "anotherDevAccountIdReceiver"
            , "publicKeyReceiver"
            , "privateKeyReceiver"
            , "domainIdReceiver"
    };

    protected String[] multiInstructionsUserSetHeaders = {
            "domainId_0",
            "domainId_1",
            "domainId_2",
            "assetDefinitionId_0",
            "assetDefinitionId_1",
            "assetDefinitionId_2_0",
            "assetDefinitionId_2_1",
            "accountIdSender_0",
            "accountIdSender_1",
            "accountIdSender_2",
            "accountIdSender_3",
            "accountIdSender_4",
            "accountIdSender_5",
            "accountIdSender_6",
            "accountIdSender_7",
            "accountIdSender_8",
            "accountIdReceiver_0",
            "accountIdReceiver_1",
            "accountIdReceiver_2",
            "accountIdReceiver_3",
            "accountIdReceiver_4",
            "accountIdReceiver_5",
            "accountIdReceiver_6",
            "accountIdReceiver_7",
            "accountIdReceiver_8",
            "assetId_0",
            "assetId_1",
            "assetId_2",
            "assetId_3",
            "assetId_4",
            "assetId_5",
            "assetId_6",
            "assetId_7",
            "assetId_8"
    };

    public Constants() throws IOException {
        this.config = new ConfigLoader();
    }
}
