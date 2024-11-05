package jp.co.soramitsu.performance.utils.smartcontract;

import jp.co.soramitsu.performance.utils.ConfigLoader;

import java.io.IOException;
import java.util.ArrayList;

public class AssetDefinitionIdGenerator {

    private ConfigLoader config;

    protected ArrayList<String> listAssetDefinitionId = new ArrayList<>();

    public AssetDefinitionIdGenerator(int count, int usersOnDomain) throws IOException {
        this.config = new ConfigLoader();
        this.listAssetDefinitionId = getAssetDefinitionList(count, config.getProperty("generatedGenesis"), usersOnDomain);
    }

    private ArrayList<String> getAssetDefinitionList(int bondCount, String pathToGenesis, int usersOnDomain) throws IOException {
        JsonParser jsonModel = new JsonParser(pathToGenesis, usersOnDomain);

        for (int i = 0; i < jsonModel.domainIds.size(); i++) {
            for (int j = 0; j < bondCount; j++) {
                listAssetDefinitionId.add("perf_xor_" + j + jsonModel.domainIds.get(i).split("-")[0] + "#" + jsonModel.domainIds.get(i));
            }
        }
        return listAssetDefinitionId;
    }
}
