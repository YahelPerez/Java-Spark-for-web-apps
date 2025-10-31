package com.rafael.collectibles;

/**
 * Standard error response object used by the API.
 */
public class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
}
