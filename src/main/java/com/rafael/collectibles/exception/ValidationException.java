package com.rafael.collectibles.exception;

/**
 * Exception thrown when there's a validation error in input data.
 */
public class ValidationException extends RuntimeException {
    private final String field;
    private final String message;

    public ValidationException(String field, String message) {
        super(String.format("Validation error for %s: %s", field, message));
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    @Override
    public String getMessage() {
        return message;
    }
}