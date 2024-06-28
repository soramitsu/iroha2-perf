package Configs;

import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.http.HttpDsl.http;

public final class Protocol {

    public static final HttpProtocolBuilder httpProtocol = http.baseUrl(System.getProperty("targetURL"))
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .acceptLanguageHeader("en-US,en;q=0.5")
            .basicAuth(System.getProperty("remoteLogin"), System.getProperty("remotePassword"));
}
