package com.finquest.exception;

/**
 * Thrown when an operation violates a domain/business rule, even though the
 * request itself is well formed (e.g. attempting to complete a quiz before
 * unlocking its module, or logging in with wrong credentials).
 * <p>
 * Maps to HTTP {@code 422 Unprocessable Entity} via {@link GlobalExceptionHandler}.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
