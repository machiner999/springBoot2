package com.example.silverapi.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    @Test
    void handleConfig_returnsServiceUnavailable() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        SilverApiConfigurationException ex = new SilverApiConfigurationException("missing config");

        ResponseEntity<ApiError> response = handler.handleConfig(ex);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CONFIGURATION_ERROR", response.getBody().error());
        assertEquals("missing config", response.getBody().message());
    }

    @Test
    void handleUnavailable_returnsBadGateway() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        SilverApiUnavailableException ex = new SilverApiUnavailableException("upstream down", null);

        ResponseEntity<ApiError> response = handler.handleUnavailable(ex);

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UPSTREAM_ERROR", response.getBody().error());
        assertEquals("upstream down", response.getBody().message());
    }
}
