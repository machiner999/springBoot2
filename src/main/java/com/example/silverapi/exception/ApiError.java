package com.example.silverapi.exception;

import java.time.Instant;

public record ApiError(
        String error,
        String message,
        Instant timestamp
) {
}
