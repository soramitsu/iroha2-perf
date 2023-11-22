package jp.co.soramitsu.load.infrastructure.Healthcheck;

public class HealthController {
    private boolean isHealthy;

    public HealthController(Boolean state) {
        this.isHealthy = state;
    }

    public boolean isHealthy() {
        return isHealthy;
    }

    public void setHealthy(boolean healthy) {
        this.isHealthy = healthy;
    }
}
