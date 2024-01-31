package jp.co.soramitsu.load.toolbox;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Grinder extends Wrench13 {
    private String baseUrl = "http://0.0.0.0:9091/metrics/job/";
    private URL transactionUrl = new URL(baseUrl + "transaction/");
    private URL subscriptionUrl = new URL(baseUrl + "subscription/");
    private URL queryUrl = new URL(baseUrl + "query/");
    private URL histogramUrl = new URL(baseUrl + "histogram/");
    private ArrayList<URL> query = new ArrayList<>();

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
                    getPliers().healthCheck(true, "Grinder");
                    System.out.println("response code: " + HttpURLConnection.HTTP_ACCEPTED + " accepted");
                } else {
                    getPliers().healthCheck(false, "Grinder");
                    System.out.println("a deletion request not accepted");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
}