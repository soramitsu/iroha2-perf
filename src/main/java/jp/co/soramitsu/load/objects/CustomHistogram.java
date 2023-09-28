package jp.co.soramitsu.load.objects;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;

import java.util.ArrayList;

public class CustomHistogram {
    private final CollectorRegistry registry;
    private final ArrayList<Histogram> histograms = new ArrayList<>();
    private final ArrayList<Counter> counters = new ArrayList<>();
    /*public static Histogram subscriptionToBlockStream;
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
    public static Histogram unauthorizedResponseTime;*/

    public static Counter subscriptionToBlockStream;
    public static Counter domainRegisterTransactionTimer;
    public static Counter accountRegisterTransactionTimer;
    public static Counter assetDefinitionRegisterTransactionTimer;
    public static Counter assetMintTransactionTimer;
    public static Counter transferAssetTransactionTimer;
    /*public static Counter createSubscriptionTransactionResponseTime;
    public static Counter rejectTransactionResponseTime;
    public static Counter commitTransactionResponseTime;
    public static Counter validatingTransactionResponseTime;
    public static Counter sendTransactionResponseTime;*/
    public static Counter findAssetsByAccountIdQueryTimer;
    public static Counter unauthorizedResponseTime;

    public CustomHistogram(){
        registry = CollectorRegistry.defaultRegistry;

        subscriptionToBlockStream = createCounter(
                "perf_subscription_block_stream"
                , "Subscription to transaction. Create a subscription to a block stream."
                /*, "environment", "project","test"*/);
        /*createSubscriptionTransactionResponseTime = createCounter(
                "create_subscription_include"
                , "Include transaction. Create a subscription to a new transaction."
                , "environment", "project","test");
        sendTransactionResponseTime = createCounter(
                "send_include"
                , "Include transaction. Send transaction."
                , "environment", "project","test");
        validatingTransactionResponseTime = createCounter(
                "validating_include"
                , "Include transaction. Validating transaction."
                , "environment", "project","test");
        commitTransactionResponseTime = createCounter(
                "commit_include"
                , "Include transaction. Commit transaction."
                , "environment", "project","test");
        rejectTransactionResponseTime = createCounter(
                "reject_include"
                , "Include transaction. Reject transaction."
                , "environment", "project","test");*/
        domainRegisterTransactionTimer = createCounter(
                "perf_domain_register"
                , "Transaction. Domain register."
                /*, "environment", "project","test"*/);
        accountRegisterTransactionTimer = createCounter(
                "perf_account_register"
                , "Transaction. Account register."
                /*, "environment", "project","test"*/);
        assetDefinitionRegisterTransactionTimer = createCounter(
                "perf_asset_definition_register"
                , "Transaction. Asset definition register."
                /*, "environment", "project","test"*/);
        assetMintTransactionTimer = createCounter(
                "perf_asset_mint"
                , "Transaction. Asset mint."
                /*, "environment", "project","test"*/);
        transferAssetTransactionTimer = createCounter(
                "perf_transfer_asset"
                , "Transaction. Transfer asset."
                /*, "environment", "project","test"*/);
        findAssetsByAccountIdQueryTimer = createCounter(
                "perf_find_assets_account_id_query"
                , "Query. Find assets by accountId."
                /*, "environment", "project","test"*/);
/*

        subscriptionToBlockStream = createHistogram(
                "subscription_block_stream_response_time"
                , "Subscription to transaction. Create a subscription to a block stream."
                , "environment", "project","test");
        createSubscriptionTransactionResponseTime = createHistogram(
                "create_subscription_include"
                , "Include transaction. Create a subscription to a new transaction."
                , "environment", "project","test");
        sendTransactionResponseTime = createHistogram(
                "send_include"
                , "Include transaction. Send transaction."
                , "environment", "project","test");
        validatingTransactionResponseTime = createHistogram(
                "validating_include"
                , "Include transaction. Validating transaction."
                , "environment", "project","test");
        commitTransactionResponseTime = createHistogram(
                "commit_include"
                , "Include transaction. Commit transaction."
                , "environment", "project","test");
        rejectTransactionResponseTime = createHistogram(
                "reject_include"
                , "Include transaction. Reject transaction."
                , "environment", "project","test");
        domainRegisterTransactionTimer = createHistogram(
                "domain_register"
                , "Transaction. Domain register."
                , "environment", "project","test");
        accountRegisterTransactionTimer = createHistogram(
                "account_register"
                , "Transaction. Account register."
                , "environment", "project","test");
        assetDefinitionRegisterTransactionTimer = createHistogram(
                "asset_definition_register"
                , "Transaction. Asset definition register."
                , "environment", "project","test");
        assetMintTransactionTimer = createHistogram(
                "asset_mint"
                , "Transaction. Asset mint."
                , "environment", "project","test");
        transferAssetTransactionTimer = createHistogram(
                "transfer_asset"
                , "Transaction. Transfer asset."
                , "environment", "project","test");
        findAssetsByAccountIdQueryTimer = createHistogram(
                "find_assets_account_id_query_response_time"
                , "Query. Find assets by accountId."
                , "environment", "project","test");
        unauthorizedResponseTime = createHistogram(
                "unauthorized_IrohaClientException"
                , "Error"
                , "environment", "project","test");
*/

        /*histograms.add(createSubscriptionTransactionResponseTime);
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
        histograms.add(unauthorizedResponseTime);*/

        /*counters.add(createSubscriptionTransactionResponseTime);
        counters.add(sendTransactionResponseTime);
        counters.add(validatingTransactionResponseTime);
        counters.add(commitTransactionResponseTime);
        counters.add(rejectTransactionResponseTime);*/
        counters.add(domainRegisterTransactionTimer);
        counters.add(accountRegisterTransactionTimer);
        counters.add(assetDefinitionRegisterTransactionTimer);
        counters.add(assetMintTransactionTimer);
        counters.add(transferAssetTransactionTimer);
        counters.add(findAssetsByAccountIdQueryTimer);
        counters.add(unauthorizedResponseTime);

        registerCounter(counters);
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

    private Counter createCounter(String name, String help/*, String label0, String label1, String label2*/){
        return Counter.build()
                .name(name)
                .help(help)
                .create();
    }
}
