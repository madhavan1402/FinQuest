package com.finquest.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.Instant;

/**
 * A generic envelope for all API responses.
 * <p>
 * Provides a consistent shape for success, error, and validation-failure
 * payloads so the frontend can rely on a stable contract instead of ad-hoc
 * {@code Map<String, Object>} bodies.
 *
 * @param <T> the type of the data payload carried by this response
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /** Whether the request completed successfully. */
    private final boolean success;

    /** Optional human-readable message (e.g. "Registered successfully"). */
    private final String message;

    /** The actual payload, or null for pure error responses. */
    private final T data;

    /** Server timestamp, useful for debugging and caching. */
    private final Instant timestamp;

    /** HTTP status code matching the response. */
    private final int status;

    public ApiResponse(boolean success, String message, T data, Instant timestamp, int status) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
        this.status = status;
    }

    // ── Static factories ──────────────────────────────────────────────────────

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, data, Instant.now(), 200);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, Instant.now(), 200);
    }

    /**
     * Builds an error envelope. Generic so callers can match the payload type
     * they declare on their handler methods.
     */
    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<>(false, message, null, Instant.now(), status);
    }
}
