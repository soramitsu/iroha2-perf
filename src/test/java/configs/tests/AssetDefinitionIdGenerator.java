package configs.tests;

import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.generated.AssetDefinitionId;

import java.io.IOException;
import java.util.ArrayList;

public class AssetDefinitionIdGenerator /*extends Constants*/ {

    protected ArrayList<String> listAssetDefinitionId = new ArrayList<>();

    public AssetDefinitionIdGenerator(int count) throws IOException {
        this.listAssetDefinitionId = getAssetDefinitionList(count , "iroha2_config/stable/5d44d59/genesis.json");
    }

    private ArrayList<String> getAssetDefinitionList(int bondCount, String pathToGenesis) throws IOException {

        JsonParser jsonModel = new JsonParser("iroha2_config/stable/5d44d59/genesis.json");

        for (int i = 0; i < jsonModel.domainIds.size(); i++) {
            for (int j = 0; j < bondCount; j++) {
                listAssetDefinitionId.add("perf_xor_" + j + jsonModel.domainIds.get(i).split("-")[0] + "#" + jsonModel.domainIds.get(i));

                /*listAssetDefinitionId.add(new AssetDefinitionId(
                        ExtensionsKt.asName("perf_xor_" + j + jsonModel.domainIds.get(i).split("-")[0]),
                        ExtensionsKt.asDomainId(jsonModel.domainIds.get(i))).toString()
                );*/
            }

        }
        return listAssetDefinitionId;
    }
 }
