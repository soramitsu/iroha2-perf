package jp.co.soramitsu.load.infrastructure.healthcheck;

import io.micronaut.http.MediaType;
import io.micronaut.management.endpoint.annotation.Endpoint;
import io.micronaut.management.endpoint.annotation.Read;
import io.micronaut.management.endpoint.annotation.Selector;

import java.util.Date;

@Endpoint(id = "date",
        prefix = "custom",
        defaultEnabled = true,
        defaultSensitive = false)
public class Healthchack {
    private Date currentDate;

    @Read(produces = MediaType.TEXT_PLAIN)
    public String currentDatePrefix(@Selector String prefix) {
        return prefix + ": " + currentDate;
    }
}
