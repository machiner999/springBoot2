package com.example.silverapi.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SilverApiPropertiesTest {

    @Test
    void gettersAndSetters() {
        SilverApiProperties properties = new SilverApiProperties();
        properties.setBaseUrl("https://silver.example");
        properties.setApiKey("secret");
        properties.setFxBaseUrl("https://fx.example");
        properties.setConnectTimeoutMillis(123);
        properties.setReadTimeoutMillis(456);

        assertEquals("https://silver.example", properties.getBaseUrl());
        assertEquals("secret", properties.getApiKey());
        assertEquals("https://fx.example", properties.getFxBaseUrl());
        assertEquals(123, properties.getConnectTimeoutMillis());
        assertEquals(456, properties.getReadTimeoutMillis());
    }
}
