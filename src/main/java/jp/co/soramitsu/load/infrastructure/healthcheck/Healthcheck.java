package jp.co.soramitsu.load.infrastructure.healthcheck;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class Healthcheck implements HealthIndicator {
    private boolean healthy;
    public boolean isApplicationHealthy(boolean condition) {
        if (condition)
            return healthy = true;
        else
            return healthy = false;
    }
    @Override
    public Health health() {
        if (healthy)
            return Health.up().build();
        else
            return Health.down().withDetail("Error", "Something went wrong").build();
    }
}
