package jp.co.soramitsu.performance.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.security.KeyPair;

@AllArgsConstructor
@Getter
public class Account {

    private String id;

    private String key;

    private String publicKey;

    private String privateKey;

    private KeyPair keyPair;

    private String genesisPublicKey;

    private String domainId;

    @Nullable
    private String Asset;

    @Nullable
    private String AssetDefinition;
}
