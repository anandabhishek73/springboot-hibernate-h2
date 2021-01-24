package com.abhishek.demo.http.client;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.UUID;

@Slf4j
public class BaseHttpClient {

    @Getter
    private final WebClient webClient;

    protected BaseHttpClient() {
        this(null);
    }

    protected BaseHttpClient(String baseUrl) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5_000)
                .doOnConnected(conn -> {
                    conn.addHandlerLast(new WriteTimeoutHandler(2));   // For writing the request body for POST
                    conn.addHandlerLast(new ReadTimeoutHandler(5));   // For reading the response body for GET
                })
                .responseTimeout(Duration.ofSeconds(10))    // To configure a response timeout for all requests
                .keepAlive(true);

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl == null ? "http://localhost:8080" : baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeaders(headers -> {
                    headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
                    headers.add(HttpHeaders.ACCEPT, "application/json");
                })
                .filter(this::addRequestId)
                .filter(this::logRequest)
                .filter(ExchangeFilterFunction.ofResponseProcessor(this::logStatusCode))
                .build();
    }

    protected Mono<ClientResponse> logRequest(ClientRequest request, ExchangeFunction next) {
        log.info("Making request to URL {} to HOST {}", request.url(), request.headers().getHost());
        return next.exchange(request);
    }

    protected Mono<ClientResponse> addRequestId(ClientRequest request, ExchangeFunction next) {
        if (!request.headers().containsKey("X-Request-Id"))
            request = ClientRequest.from(request)
                    .headers(httpHeaders -> httpHeaders.add("X-Request-Id", UUID.randomUUID().toString()))
                    .build();
        log.info("Request ID is {}", request.headers().get("X-Request-Id"));
        return next.exchange(request);
    }

    protected Mono<ClientResponse> logStatusCode(ClientResponse response) {
        log.info("Status Code is {} : {}", response.statusCode(), response.statusCode().getReasonPhrase());
        log.debug("Headers are {}", response.headers().asHttpHeaders());
        return Mono.just(response);
    }
}
