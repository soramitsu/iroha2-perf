package jp.co.soramitsu.performance.utils.genesis;

import io.ipfs.multihash.Multihash;
import jp.co.soramitsu.iroha2.CryptoUtils;
import jp.co.soramitsu.iroha2.DigestFunction;
import jp.co.soramitsu.performance.utils.Constants;
import jp.co.soramitsu.performance.models.Account;
import jp.co.soramitsu.performance.models.Domain;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static jp.co.soramitsu.iroha2.CryptoUtils.generateKeyPair;
import static jp.co.soramitsu.iroha2.ExtensionsKt.toIrohaPublicKey;

public class CsvGenerator extends Constants {

    public CsvGenerator(int countOfDomain, int countOfAccount) throws IOException {
        super();
        generate(standardUserSetHeaders, countOfDomain, countOfAccount);
    }

    private void generate(String[] headers, int countOfDomain, int countOfAccount) throws IOException {
        csvWriter(headers);
        for (int i = 0; i < countOfDomain; i++) {
            String domainId = "perf_domain_" + UUID.randomUUID().toString().split("-")[0] + "_" + System.currentTimeMillis();
            String domainLogo = null;
            String domainKey = "value";

            Domain domain = new Domain(
                    domainId,
                    domainLogo,
                    domainKey
            );

            domainIds.add(domain);
            for (int j = 0; j < countOfAccount; j++) {
                Map<String, String> keyPairForCsvSender = new HashMap<>();
                Map<String, String> keyPairForCsvReceiver = new HashMap<>();

                //key pair for csv
                KeyPair keyPairSender = generateKeyPair();
                KeyPair keyPairReceiver = generateKeyPair();
                keyPairForCsvSender = keyPairForCsv(keyPairSender);
                keyPairForCsvReceiver = keyPairForCsv(keyPairReceiver);
                String accountIdSender = keyPairForCsvSender.get("publicKey") + "@" + domainIds.get(i).getId();
                String accountIdReceiver = keyPairForCsvReceiver.get("publicKey") + "@" + domainIds.get(i).getId();

                //public key for genesis
                String genesisAccountIdSender = accountPublicKeyForGenesis(keyPairSender);
                String genesisAccountIdReceiver = accountPublicKeyForGenesis(keyPairReceiver);

                String assetDefinitionId = UUID.randomUUID().toString().split("-")[0] + "_" + System.currentTimeMillis();

                String assetDefinitionSender = "perf_asset_sender_" +
                        assetDefinitionId +
                        "#" +
                        domainIds.get(i).getId();

                String assetSender = "perf_asset_sender_" + assetDefinitionId + "##" + genesisAccountIdSender + "@" + domainIds.get(i).getId();

                Account accountSenderCSV = new Account(accountIdSender,
                        "value",
                        keyPairForCsvSender.get("publicKey"),
                        keyPairForCsvSender.get("privateKey"),
                        keyPairSender,
                        genesisAccountIdSender,
                        domainIds.get(i).getId(),
                        assetSender,
                        assetDefinitionSender
                );

                Account accountReceiverCSV = new Account(accountIdReceiver,
                        "value",
                        keyPairForCsvReceiver.get("publicKey"),
                        keyPairForCsvReceiver.get("privateKey"),
                        keyPairReceiver,
                        genesisAccountIdReceiver,
                        domainIds.get(i).getId(),
                        null,
                        null
                );

                accountIds.add(accountSenderCSV);
                accountIds.add(accountReceiverCSV);

                String[] line = {
                        accountSenderCSV.getId(),
                        accountSenderCSV.getPublicKey(),
                        accountSenderCSV.getPrivateKey(),
                        domainIds.get(i).getId(),
                        assetDefinitionSender,
                        assetSender,
                        accountReceiverCSV.getId(),
                        accountReceiverCSV.getPublicKey(),
                        accountReceiverCSV.getPrivateKey(),
                        domainIds.get(i).getId()
                };

                csvWriter(line);
            }
        }
        csvWriterClose();
    }

    private String accountPublicKeyForGenesis(KeyPair keyPair) throws IOException {
        String hexPublicKey;
        ByteArrayOutputStream publicKey = new ByteArrayOutputStream();
        ByteArrayOutputStream privateKey = new ByteArrayOutputStream(); //test

        Multihash.putUvarint(publicKey, Long.valueOf(DigestFunction.Ed25519.getIndex()));
        Multihash.putUvarint(publicKey, Long.valueOf(toIrohaPublicKey(keyPair.getPublic()).getPayload().length));

        publicKey.write(toIrohaPublicKey(keyPair.getPublic()).getPayload());

        //output: ed01209d687da7e90e3160574cab4dc88dfd7dbfd052df51531cadcc80dceafea06082
        hexPublicKey = Hex.toHexString(publicKey.toByteArray());

        return hexPublicKey;
    }

    private Map<String, String> keyPairForCsv(KeyPair keyPair) throws IOException {
        Map<String, String> keyPairMap = new HashMap<>();

        ByteArrayOutputStream publicKey = new ByteArrayOutputStream();
        ByteArrayOutputStream privateKey = new ByteArrayOutputStream();

        Multihash.putUvarint(publicKey, Long.valueOf(DigestFunction.Ed25519.getIndex()));
        Multihash.putUvarint(publicKey, Long.valueOf(toIrohaPublicKey(keyPair.getPublic()).getPayload().length));

        publicKey.write(toIrohaPublicKey(keyPair.getPublic()).getPayload());
        privateKey.write(CryptoUtils.bytes(keyPair.getPrivate()));

        //output 9d687da7e90e3160574cab4dc88dfd7dbfd052df51531cadcc80dceafea06082
        keyPairMap.put("publicKey", Hex.toHexString(CryptoUtils.bytes(keyPair.getPublic())));
        //output 4b52c56e4a254ebcb124d62a1c034e26d35560114793c1c768e7202be34c52b3
        keyPairMap.put("privateKey", Hex.toHexString(CryptoUtils.bytes(keyPair.getPrivate())));

        return keyPairMap;
    }

    private void csvWriter(String[] any) {
        writer.writeNext(any);
    }

    private void csvWriterClose() throws IOException {
        writer.close();
    }
}
