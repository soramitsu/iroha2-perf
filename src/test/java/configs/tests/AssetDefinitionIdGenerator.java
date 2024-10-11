package configs.tests;

import healper.Constants;
import jp.co.soramitsu.iroha2.ExtensionsKt;
import jp.co.soramitsu.iroha2.generated.AssetDefinitionId;
import scala.collection.immutable.Stream;

import java.io.IOException;
import java.util.ArrayList;

public class AssetDefinitionIdGenerator extends Constants {

    protected ArrayList<AssetDefinitionId> listAssetDefinitionId;

    public AssetDefinitionIdGenerator(int count) throws IOException {
        this.listAssetDefinitionId = getAssetDefinitionList(count ,PATH_TO_GENESIS_RC20);
    }

    private ArrayList<AssetDefinitionId> getAssetDefinitionList(int bondCount, String pathToGenesis) throws IOException {

        JsonParser jsonModel = new JsonParser("iroha2_config/stable/5d44d59/genesis.json");

        for (int i = 0; i < jsonModel.domainIds.size(); i++) {
            for (int j = 0; j < bondCount; j++) {
                listAssetDefinitionId.add(new AssetDefinitionId(
                        ExtensionsKt.asName("perf_xor_" + j + jsonModel.domainIds.get(i).split("-")[0]),
                        ExtensionsKt.asDomainId(jsonModel.domainIds.get(i)))
                );
            }

        }
        return listAssetDefinitionId;
    }
 }
