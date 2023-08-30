package jp.co.soramitsu.load;


import com.google.common.net.HttpHeaders;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.util.HashMap;
import java.util.Map;

import static io.gatling.javaapi.http.HttpDsl.http;

public final class Protocols {

    public static final HttpProtocolBuilder httpProtocol =
        http.contentTypeHeader("application/json; charset=UTF-8")
            .header(HttpHeaders.CONNECTION, "keep-alive")
            .header(HttpHeaders.ACCEPT, "*/*")
            .header(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, br")
            .disableCaching()
            .disableWarmUp()
            .disableFollowRedirect();
}
