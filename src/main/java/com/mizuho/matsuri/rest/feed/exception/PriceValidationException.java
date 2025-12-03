package com.mizuho.matsuri.rest.feed.exception;

public class PriceValidationException extends Exception {
    public PriceValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PriceValidationException(String message) {
        super(message);
    }
}
