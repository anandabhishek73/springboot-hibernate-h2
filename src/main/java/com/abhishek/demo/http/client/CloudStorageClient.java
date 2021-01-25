package com.abhishek.demo.http.client;

import com.abhishek.demo.http.config.CloudStorageClientConfig;
import com.abhishek.demo.http.model.DocumentUploadRequest;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientRequest;

import java.time.Duration;

/**
 * The is Spring WebFlux based non-blocking I/O WebClient. Check out:
 * Applicability : https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux-framework-choice
 * Performance Benefits : https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux-performance
 */
@Component
public class CloudStorageClient extends BaseHttpClient {

    private final String defaultClientName = "CKYC";
    private final String ATTRIBUTE_CLIENT_NAME = "client";

    @Getter
    WebClient webClient;

    @Autowired
    private CloudStorageClient(CloudStorageClientConfig cloudStorageClientConfig) {
        super();

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, cloudStorageClientConfig.getRequestTimeout())
                .doOnConnected(conn -> {
                    conn.addHandlerLast(new WriteTimeoutHandler(10));   // For writing the request body for POST
                    conn.addHandlerLast(new ReadTimeoutHandler(10));    // For reading the response body for GET
                })
                .responseTimeout(Duration.ofSeconds(10))    // To configure a response timeout for all requests
                .keepAlive(false);

        this.webClient = super.getWebClient().mutate()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(cloudStorageClientConfig.getDomain())
                .defaultHeaders(headers -> {
                    headers.add(HttpHeaders.ACCEPT, MimeTypeUtils.APPLICATION_JSON_VALUE);
                    headers.add(HttpHeaders.ACCEPT, MediaType.MULTIPART_FORM_DATA_VALUE);
                })
//                .filter(ExchangeFilterFunctions.basicAuthentication(user, password))
                .filter(this::addJwtToken)    // Interceptor. JWT Token appender
                .build();

    }

    private Mono<ClientResponse> addJwtToken(ClientRequest request, ExchangeFunction next) {
        if (!request.headers().containsKey("X-JWT-Token")) {
            String clientName = (String) request.attribute(ATTRIBUTE_CLIENT_NAME).orElse(defaultClientName);
            // String jwtToken = TokenManager.getJwtToken(clientName);
            String jwtToken = clientName + ":TOKEN";
            ClientRequest filtered = ClientRequest.from(request)
                    .headers(headers -> headers.add("X-JWT-Token", jwtToken))
                    .build();
            return next.exchange(filtered);
        }
        return next.exchange(request);
    }

    public Mono<String> getDocument(String docId) {
        return webClient.get()
                .uri("/path/to/download/{docId}", docId)
                .accept(MediaType.APPLICATION_JSON, MediaType.MULTIPART_FORM_DATA)
                .attribute(ATTRIBUTE_CLIENT_NAME, defaultClientName)
                .httpRequest(httpRequest -> {
                    HttpClientRequest reactorRequest = httpRequest.getNativeRequest();
                    reactorRequest.responseTimeout(Duration.ofSeconds(2));
                })
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(String.class);
                    } else if (response.statusCode().is4xxClientError()) {
                        // Suppress error status code
                        return response.bodyToMono(String.class);
                    } else {
                        // Turn to error
                        return response.createException().flatMap(Mono::error);
                    }
                });
//                .retrieve()
//                .onStatus(HttpStatus::is5xxServerError, clientResponse -> {})
//                .onStatus(HttpStatus::is4xxClientError, clientResponse -> {})
//                .onStatus(HttpStatus::is2xxSuccessful, clientResponse -> {})
//                .onStatus(HttpStatus.TOO_MANY_REQUESTS, clientResponse -> /* Backpressure */{})
//                .bodyToMono(String.class);
    }

    public Mono<String> uploadDocument(DocumentUploadRequest.CloudDocument document) {
        return uploadDocument(document, defaultClientName);
    }

    public Mono<String> uploadDocument(DocumentUploadRequest.CloudDocument document, String clientName) {
        return webClient.post()
                .uri("/path/to/upload/")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .attribute(ATTRIBUTE_CLIENT_NAME, clientName) // for JWT interceptor
//                .body(new MultipartBodyBuilder)
                // See: https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux-client-body-multipart
                .body(BodyInserters.fromMultipartData(ATTRIBUTE_CLIENT_NAME, clientName).with("file", document))
                .httpRequest(httpRequest -> {
                    HttpClientRequest reactorRequest = httpRequest.getNativeRequest();
                    reactorRequest.responseTimeout(Duration.ofSeconds(2));
                })
                .retrieve()
                .bodyToMono(String.class);
    }

}
