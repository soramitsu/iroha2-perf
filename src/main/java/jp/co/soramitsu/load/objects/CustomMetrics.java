package jp.co.soramitsu.load.objects;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import jp.co.soramitsu.load.toolbox.Pliers;

import java.util.ArrayList;

public class CustomMetrics {
    private final CollectorRegistry registry;
    private final ArrayList<Histogram> histograms = new ArrayList<>();
    private final ArrayList<Counter> counters = new ArrayList<>();
    public static Histogram subscriptionToBlockStreamTimer;
    public static Histogram domainRegisterTimer;
    public static Histogram accountRegisterTimer;
    public static Histogram assetDefinitionRegisterTimer;
    public static Histogram assetMintTimer;
    public static Histogram transferAssetTimer;
    public static Histogram findAssetsByAccountIdQueryTimer;
    public static Counter subscriptionToBlockStreamCount;
    public static Counter domainRegisterCount;
    public static Counter accountRegisterCount;
    public static Counter assetDefinitionRegisterCount;
    public static Counter assetMintCount;
    public static Counter transferAssetCount;
    public static Counter findAssetsByAccountIdQueryCount;
    public static Counter unauthorizedResponseTime;
    public static Counter subscriptionToBlockStreamErrorCount;
    public static Counter transferAssetErrorCount;
    public static Counter domainRegisterErrorCount;
    public static Counter accountRegisterErrorCount;
    public static Counter assetDefinitionRegisterErrorCount;
    public static Counter assetMintErrorCount;
    public static Counter findAssetsByAccountIdQueryErrorCount;
    private Pliers pliers = new Pliers();

    public CustomMetrics(){
        registry = CollectorRegistry.defaultRegistry;
        try {
            subscriptionToBlockStreamCount = createCounter(
                    "subscription_block_stream_performance"
                    , "Subscription to transaction. Create a subscription to a block stream."
                    , "environment", "project","test");
            subscriptionToBlockStreamErrorCount = createCounter(
                    "subscription_block_stream_KO_performance"
                    , "Subscription to transaction. Create a subscription to a block stream."
                    , "environment", "project","test");
            domainRegisterCount = createCounter(
                    "domain_register_performance"
                    , "Transaction. Domain register."
                    , "environment", "project","test");
            domainRegisterErrorCount = createCounter(
                    "domain_register_KO_performance"
                    , "Transaction. Domain register."
                    , "environment", "project","test");
            accountRegisterCount = createCounter(
                    "account_register_performance"
                    , "Transaction. Account register."
                    , "environment", "project","test");
            accountRegisterErrorCount = createCounter(
                    "account_register_KO_performance"
                    , "Transaction. Account register."
                    , "environment", "project","test");
            assetDefinitionRegisterCount = createCounter(
                    "asset_definition_register_performance"
                    , "Transaction. Asset definition register."
                    , "environment", "project","test");
            assetDefinitionRegisterErrorCount = createCounter(
                    "asset_definition_register_KO_performance"
                    , "Transaction. Asset definition register."
                    , "environment", "project","test");
            assetMintCount = createCounter(
                    "asset_mint_performance"
                    , "Transaction. Asset mint."
                    , "environment", "project","test");
            assetMintErrorCount = createCounter(
                    "asset_mint_KO_performance"
                    , "Transaction. Asset mint."
                    , "environment", "project","test");
            transferAssetCount = createCounter(
                    "transfer_asset_performance"
                    , "Transaction. Transfer asset."
                    , "environment", "project","test");
            transferAssetErrorCount = createCounter(
                    "transfer_asset_KO_performance"
                    , "Transaction. Transfer asset."
                    , "environment", "project","test");
            findAssetsByAccountIdQueryCount = createCounter(
                    "find_assets_account_id_query_performance"
                    , "Query. Find assets by accountId."
                    , "environment", "project","test");
            findAssetsByAccountIdQueryErrorCount = createCounter(
                    "find_assets_account_id_query_KO_performance"
                    , "Query. Find assets by accountId."
                    , "environment", "project","test");
            subscriptionToBlockStreamTimer = createHistogram(
                    "subscription_block_stream_perf"
                    , "Subscription to transaction. Create a subscription to a block stream."
                    , "environment", "project","test");
            domainRegisterTimer = createHistogram(
                    "domain_register_perf"
                    , "Transaction. Domain register."
                    , "environment", "project","test");
            accountRegisterTimer = createHistogram(
                    "account_register_perf"
                    , "Transaction. Account register."
                    , "environment", "project","test");
            assetDefinitionRegisterTimer = createHistogram(
                    "asset_definition_register_perf"
                    , "Transaction. Asset definition register."
                    , "environment", "project","test");
            assetMintTimer = createHistogram(
                    "asset_mint_perf"
                    , "Transaction. Asset mint."
                    , "environment", "project","test");
            transferAssetTimer = createHistogram(
                    "transfer_asset_perf"
                    , "Transaction. Transfer asset."
                    , "environment", "project","test");
            findAssetsByAccountIdQueryTimer = createHistogram(
                    "find_assets_account_id_query_perf"
                    , "Query. Find assets by accountId."
                    , "environment", "project","test");

            counters.add(domainRegisterCount);
            counters.add(accountRegisterCount);
            counters.add(assetDefinitionRegisterCount);
            counters.add(assetMintCount);
            counters.add(transferAssetCount);
            counters.add(findAssetsByAccountIdQueryCount);
            counters.add(unauthorizedResponseTime);
            counters.add(subscriptionToBlockStreamErrorCount);
            counters.add(domainRegisterErrorCount);
            counters.add(accountRegisterErrorCount);
            counters.add(assetDefinitionRegisterErrorCount);
            counters.add(assetMintErrorCount);
            counters.add(transferAssetErrorCount);
            counters.add(findAssetsByAccountIdQueryErrorCount);

            histograms.add(subscriptionToBlockStreamTimer);
            histograms.add(domainRegisterTimer);
            histograms.add(accountRegisterTimer);
            histograms.add(assetDefinitionRegisterTimer);
            histograms.add(assetMintTimer);
            histograms.add(transferAssetTimer);
            histograms.add(findAssetsByAccountIdQueryTimer);

            registerCounter(counters);
            registerHistograms(histograms);
            pliers.healthCheck(true, "CustomHistogram");
        } catch (Exception ex) {
            System.out.println("Something went wrong: " + ex.getMessage());
            System.out.println("Something went wrong: " + ex.getStackTrace().toString());
            pliers.healthCheck(false, "CustomHistogram");
        }
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

    private void registerCounter(ArrayList<Counter> countersList){
        for (Counter counter: countersList) {
            registry.register(counter);
        }
    }

    private Counter createCounter(String name, String help, String label0, String label1, String label2){
        return Counter.build()
                .name(name)
                .help(help)
                .labelNames(label0, label1, label2)
                .create();
    }
}
