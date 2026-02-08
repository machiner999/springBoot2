package com.example.silverapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SilverApiApplicationTest {

    @Test
    void hasSpringBootApplicationAnnotation() {
        assertTrue(SilverApiApplication.class.isAnnotationPresent(SpringBootApplication.class));
    }
}
