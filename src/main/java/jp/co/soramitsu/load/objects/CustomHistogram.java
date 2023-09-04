package jp.co.soramitsu.load.objects;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;

import java.util.ArrayList;

public class CustomHistogram {
    private final CollectorRegistry registry;
    private final ArrayList<Histogram> histograms = new ArrayList<>();
    public static Histogram subscriptionToBlockStream;
    public static Histogram domainRegisterTransactionTimer;
    public static Histogram accountRegisterTransactionTimer;
    public static Histogram assetDefinitionRegisterTransactionTimer;
    public static Histogram assetMintTransactionTimer;
    public static Histogram transferAssetTransactionTimer;
    public static Histogram createSubscriptionTransactionResponseTime;
    public static Histogram rejectTransactionResponseTime;
    public static Histogram commitTransactionResponseTime;
    public static Histogram validatingTransactionResponseTime;
    public static Histogram sendTransactionResponseTime;
    public static Histogram findAssetsByAccountIdQueryTimer;

    public CustomHistogram(){
        registry = CollectorRegistry.defaultRegistry;
        subscriptionToBlockStream = createHistogram(
                "subscription_block_stream_response_time"
                , "Subscription to transaction. Create a subscription to a block stream."
                , "environment", "project","test");
        createSubscriptionTransactionResponseTime = createHistogram(
                "create_subscription_include_transaction_response_time"
                , "Include transaction. Create a subscription to a new transaction."
                , "environment", "project","test");
        sendTransactionResponseTime = createHistogram(
                "send_include_transaction_response_time"
                , "Include transaction. Send transaction."
                , "environment", "project","test");
        validatingTransactionResponseTime = createHistogram(
                "validating_include_transaction_response_time"
                , "Include transaction. Validating transaction."
                , "environment", "project","test");
        commitTransactionResponseTime = createHistogram(
                "commit_include_transaction_response_time"
                , "Include transaction. Commit transaction."
                , "environment", "project","test");
        rejectTransactionResponseTime = createHistogram(
                "reject_include_transaction_response_time"
                , "Include transaction. Reject transaction."
                , "environment", "project","test");
        domainRegisterTransactionTimer = createHistogram(
                "domain_register_transaction_response_time"
                , "Transaction. Domain register."
                , "environment", "project","test");
        accountRegisterTransactionTimer = createHistogram(
                "account_register_transaction_response_time"
                , "Transaction. Account register."
                , "environment", "project","test");
        assetDefinitionRegisterTransactionTimer = createHistogram(
                "asset_definition_register_transaction_response_time"
                , "Transaction. Asset definition register."
                , "environment", "project","test");
        assetMintTransactionTimer = createHistogram(
                "asset_mint_transaction_response_time"
                , "Transaction. Asset mint."
                , "environment", "project","test");
        transferAssetTransactionTimer = createHistogram(
                "transfer_asset_transaction_response_time"
                , "Transaction. Transfer asset."
                , "environment", "project","test");
        findAssetsByAccountIdQueryTimer = createHistogram(
                "find_assets_account_id_query_response_time"
                , "Query. Find assets by accountId."
                , "environment", "project","test");


        histograms.add(createSubscriptionTransactionResponseTime);
        histograms.add(sendTransactionResponseTime);
        histograms.add(validatingTransactionResponseTime);
        histograms.add(commitTransactionResponseTime);
        histograms.add(rejectTransactionResponseTime);
        histograms.add(domainRegisterTransactionTimer);
        histograms.add(accountRegisterTransactionTimer);
        histograms.add(assetDefinitionRegisterTransactionTimer);
        histograms.add(assetMintTransactionTimer);
        histograms.add(transferAssetTransactionTimer);
        histograms.add(findAssetsByAccountIdQueryTimer);

        registerHistograms(histograms);
    }

    private void registerHistograms(ArrayList<Histogram> histogramsList){
        for (Histogram histogram: histogramsList) {
            registry.register(histogram);
        }
    }

    private Histogram createHistogram(String name, String help, String label0, String label1, String label2){
        return Histogram.build()
                .name(name)
                .help(help)
                .labelNames(label0, label1, label2)
                .buckets(0.2, 0.5, 0.8, 1.0, 1.3, 1.6, 2.0)
                .create();
    }
}
