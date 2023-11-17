package jp.co.soramitsu.load.toolbox;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Grinder extends Wrench13{
    String baseUrl = "http://pushgateway:9091/metrics/job/";
    URL transactionUrl = new URL(baseUrl + "transaction/");
    URL subscriptionUrl = new URL(baseUrl + "subscription/");
    URL queryUrl = new URL(baseUrl + "query/");
    URL histogramUrl = new URL(baseUrl + "histogram/");
    ArrayList<URL> query = new ArrayList<>();

    public Grinder() throws MalformedURLException {
        cleanUpMetrics();
    }
    private void cleanUpMetrics(){
        query.add(transactionUrl);
        query.add(subscriptionUrl);
        query.add(queryUrl);
        query.add(histogramUrl);

        for (URL url: query) {
            try{
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");
                int responseCode = connection.getResponseCode();
                System.out.println("a deletion request has been sent: " + url);

                if (responseCode == HttpURLConnection.HTTP_ACCEPTED) {
                    System.out.println("response code: " + HttpURLConnection.HTTP_ACCEPTED + " accepted");
                    healthcheck.isApplicationHealthy(true);
                } else {
                    System.out.println("a deletion request not accepted");
                    healthcheck.isApplicationHealthy(false);
                }
            } catch (Exception e) {
                healthcheck.isApplicationHealthy(false);
                throw new RuntimeException(e);
            }
        }

    }
}