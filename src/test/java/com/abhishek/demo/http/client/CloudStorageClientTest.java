package com.abhishek.demo.http.client;

import com.abhishek.demo.http.model.DocumentUploadRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class CloudStorageClientTest {

    @Autowired
    CloudStorageClient cloudStorageClient;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getDocument() {
        log.info("Response is : {}", cloudStorageClient.getDocument("1234").block());
    }

    @Test
    void uploadDocument() {
        log.info("Response is : {}", cloudStorageClient.uploadDocument(new DocumentUploadRequest.CloudDocument()).block());
    }
}