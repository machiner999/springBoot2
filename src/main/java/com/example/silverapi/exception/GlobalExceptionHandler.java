package com.example.silverapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SilverApiConfigurationException.class)
    public ResponseEntity<ApiError> handleConfig(SilverApiConfigurationException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ApiError("CONFIGURATION_ERROR", ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(SilverApiUnavailableException.class)
    public ResponseEntity<ApiError> handleUnavailable(SilverApiUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ApiError("UPSTREAM_ERROR", ex.getMessage(), Instant.now()));
    }
}
