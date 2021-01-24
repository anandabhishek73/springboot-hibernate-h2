package com.abhishek.demo.http.client;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootTest
public class BaseHttpClientTest {

    private final BaseHttpClient baseHttpClient = new BaseHttpClient();

    @Test
    void testBaseHttpClient(){
        String googleResponse = baseHttpClient.getWebClient().get()
                .uri("https://www.google.com?q=abhishek")
                .headers(h->h.setAccept(Arrays.asList(MediaType.APPLICATION_JSON)))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        log.info("Google search response is \n{}", googleResponse);
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void logRequest() {
    }
}