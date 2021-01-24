package com.abhishek.demo.http.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "client.cloud-storage")
@PropertySource("classpath:httpClient.properties")
public class CloudStorageClientConfig {

    private String domain;

    /**
     * Request timeout in milliseconds. Default in annotation : 2000
     */
    private Integer requestTimeout;

}
