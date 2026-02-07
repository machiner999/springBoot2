package com.example.silverapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    RestClient silverApiRestClient(SilverApiProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    @Bean
    RestClient fxRestClient(SilverApiProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getFxBaseUrl())
                .build();
    }
}
