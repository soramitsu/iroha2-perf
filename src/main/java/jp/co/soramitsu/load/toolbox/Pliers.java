package jp.co.soramitsu.load.toolbox;
import spark.Spark;

public class Pliers extends Wrench13 {
    private boolean isHealthy = false;

    public Pliers (){
        Spark.init();
    }

    private void currentState(boolean check){
        Spark.get("/health", (request, response) -> {
            if (check) {
                response.status(200);
                return "healthy";
            } else {
                response.status(500);
                return "unhealthy";
            }
        });
    }

    private void setHealthy(boolean state, String service){
        if (state) {
            System.out.println(service + " is alive he will live");
        } else {
            System.out.println(service + " is alive he will live");
        }
        isHealthy = state;
    }

    public void healthCheck(boolean currentState, String services){
        switch (services){
            case "Grinder":
                if (currentState) {
                    setHealthy(currentState, "Grinder");
                    currentState(isHealthy);
                }
                else {
                    setHealthy(currentState, "Grinder");
                    currentState(isHealthy);
                }
                break;
            case "CustomHistogram":
                if (currentState) {
                    setHealthy(currentState, "CustomHistogram");
                    currentState(isHealthy);
                }
                else {
                    setHealthy(currentState, "CustomHistogram");
                    currentState(isHealthy);
                }
                break;
            case "Iroha2SetUp":
                if (currentState) {
                    setHealthy(currentState, "Iroha2SetUp");
                    currentState(isHealthy);
                }
                else {
                    setHealthy(currentState, "Iroha2SetUp");
                    currentState(isHealthy);
                }
                break;
            case "TransferAssets":
                if (currentState) {
                    setHealthy(currentState, "TransferAssets");
                    currentState(isHealthy);
                }
                else {
                    setHealthy(currentState, "TransferAssets");
                    currentState(isHealthy);
                }
                break;
            case "TransferAssetsQueryStatus":
                if (currentState) {
                    setHealthy(currentState, "TransferAssetsQueryStatus");
                    currentState(isHealthy);
                }
                else {
                    setHealthy(currentState, "TransferAssetsQueryStatus");
                    currentState(isHealthy);
                }
                break;
        }
    }
}
