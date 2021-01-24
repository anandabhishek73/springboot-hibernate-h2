package com.abhishek.demo.http.model;

public class DocumentUploadRequest {
    CloudDocument document;
    String client;
    String customerId;

    public static class CloudDocument {
        String dmsId;
    }
}
