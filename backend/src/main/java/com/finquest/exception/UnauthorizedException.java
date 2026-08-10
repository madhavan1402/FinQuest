package com.finquest.exception;

/**
 * Thrown when authentication fails (e.g. invalid email or password during login).
 * <p>
 * Maps to HTTP {@code 401 Unauthorized} via {@link GlobalExceptionHandler}.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
