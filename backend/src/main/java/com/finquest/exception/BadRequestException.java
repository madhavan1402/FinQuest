package com.finquest.exception;

/**
 * Thrown when a client sends a well-formed request that is semantically
 * invalid (e.g. a negative salary, an empty decisions list, or a missing
 * field that bean validation could not catch).
 * <p>
 * Maps to HTTP {@code 400 Bad Request} via {@link GlobalExceptionHandler}.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
