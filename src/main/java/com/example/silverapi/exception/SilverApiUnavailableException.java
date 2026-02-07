package com.example.silverapi.exception;

public class SilverApiUnavailableException extends RuntimeException {
    public SilverApiUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
