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

        assertEquals("https://silver.example", properties.getBaseUrl());
        assertEquals("secret", properties.getApiKey());
        assertEquals("https://fx.example", properties.getFxBaseUrl());
    }
}
