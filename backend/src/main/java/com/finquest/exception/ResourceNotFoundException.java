package com.finquest.exception;

/**
 * Thrown when a requested resource (e.g. a User, Quiz, or Module) does not exist.
 * <p>
 * Maps to HTTP 404 Not Found via {@code GlobalExceptionHandler}.
 * Replaces the previous pattern of throwing a generic {@link RuntimeException}
 * with an English message, which made it impossible to distinguish a "missing
 * item" from a genuine server fault.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Object identifier) {
        super(resource + " not found" + (identifier != null ? ": " + identifier : ""));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
