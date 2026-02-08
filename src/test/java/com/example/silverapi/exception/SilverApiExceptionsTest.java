package com.example.silverapi.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class SilverApiExceptionsTest {

    @Test
    void configurationExceptionStoresMessage() {
        SilverApiConfigurationException ex = new SilverApiConfigurationException("bad config");
        assertEquals("bad config", ex.getMessage());
    }

    @Test
    void unavailableExceptionStoresMessageAndCause() {
        RuntimeException cause = new RuntimeException("root");
        SilverApiUnavailableException ex = new SilverApiUnavailableException("upstream", cause);

        assertEquals("upstream", ex.getMessage());
        assertSame(cause, ex.getCause());
    }
}
