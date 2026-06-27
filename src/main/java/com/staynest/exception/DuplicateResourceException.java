package com.staynest.exception;

/**
 * Thrown when trying to create something that must be unique but isn't -
 * e.g. registering with an email that's already taken.
 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
