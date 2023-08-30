package jp.co.soramitsu.load.objects;

import jp.co.soramitsu.iroha2.generated.*;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

public class AnotherDev {

    private AccountId anotherDevAccountId;
    private KeyPair  anotherDevKeyPair;
    private DomainId anotherDevDomainId;
    private AssetDefinitionId assetDefinitionId;
    private AssetId anotherDevAssetId;
    private AssetValue assetValue;

    public AssetValue getAssetValue() {
        return assetValue;
    }

    public void setAssetValue(AssetValue assetValue) {
        this.assetValue = assetValue;
    }
    public AccountId getAnotherDevAccountId() {
        return anotherDevAccountId;
    }

    public void setAnotherDevAccountId(AccountId anotherDevAccountId) {
        this.anotherDevAccountId = anotherDevAccountId;
    }

    public KeyPair getAnotherDevKeyPair() {
        return anotherDevKeyPair;
    }

    public void setAnotherDevKeyPair(KeyPair anotherDevKeyPair) {
        this.anotherDevKeyPair = anotherDevKeyPair;
    }

    public DomainId getAnotherDevDomainId() {
        return anotherDevDomainId;
    }

    public void setAnotherDevDomainId(DomainId anotherDevDomainId) {
        this.anotherDevDomainId = anotherDevDomainId;
    }

    public AssetDefinitionId getAssetDefinitionId() {
        return assetDefinitionId;
    }

    public void setAssetDefinitionId(AssetDefinitionId assetDefinitionId) {
        this.assetDefinitionId = assetDefinitionId;
    }

    public AssetId getAnotherDevAssetId() {
        return anotherDevAssetId;
    }

    public void setAnotherDevAssetId(AssetId anotherDevAssetId) {
        this.anotherDevAssetId = anotherDevAssetId;
    }

}
