package com.example.silverapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    RestClient silverApiRestClient(SilverApiProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = buildRequestFactory(properties);
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .requestFactory(requestFactory)
                .build();
    }

    @Bean
    RestClient fxRestClient(SilverApiProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = buildRequestFactory(properties);
        return RestClient.builder()
                .baseUrl(properties.getFxBaseUrl())
                .requestFactory(requestFactory)
                .build();
    }

    private static SimpleClientHttpRequestFactory buildRequestFactory(SilverApiProperties properties) {
        int connectTimeoutMillis = clampTimeoutMillis(properties.getConnectTimeoutMillis());
        int readTimeoutMillis = clampTimeoutMillis(properties.getReadTimeoutMillis());

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeoutMillis);
        factory.setReadTimeout(readTimeoutMillis);
        return factory;
    }

    private static int clampTimeoutMillis(int value) {
        return value <= 0 ? 1 : value;
    }
}
