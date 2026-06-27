package com.staynest.exception;

/**
 * Thrown when code looks up an entity by ID (or email, etc.) and it
 * doesn't exist - e.g. GET /properties/view/9999 where no property
 * with id 9999 exists. Caught by GlobalExceptionHandler and turned
 * into a friendly 404 page / JSON response instead of a raw stack trace.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
