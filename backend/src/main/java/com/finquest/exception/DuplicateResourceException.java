package com.finquest.exception;

/**
 * Thrown when an attempt is made to create a resource that already exists
 * (e.g. registering with an email that is already in use).
 * <p>
 * Maps to HTTP {@code 409 Conflict} via {@link GlobalExceptionHandler}.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
