package com.rafael.collectibles;

/**
 * Simple success response for operations like DELETE.
 */
public class SuccessResponse {
    private String message;

    public SuccessResponse(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
}
