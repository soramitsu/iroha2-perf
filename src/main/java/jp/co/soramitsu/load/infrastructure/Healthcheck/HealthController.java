package jp.co.soramitsu.load.infrastructure.Healthcheck;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController implements HealthIndicator {

    @Override
    public Health health() {
        return Health.up().build();
    }

    @GetMapping("/actuator/health")
    public String healthCheck() {
        return "Health Check Passed!";
    }
}