package com.example.ecommerce.shared.payload;

import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;

/**
 * Represents a simple error response that is sent to the client in case of an error.
 * This class encapsulates the HTTP status, error message, and the timestamp of the error
 * occurrence.
 * <p>
 * The {@link SimpleErrorResponse} is typically used to provide consistent error responses
 * in the API. It includes:
 *
 * @param status    The HTTP status code or description as a string
 * @param message   A detailed message describing the error
 * @param timestamp The timestamp when the error occurred
 */
public record SimpleErrorResponse(
        String status,
        String message,
        LocalDateTime timestamp
) {

    public SimpleErrorResponse(HttpStatusCode status, String message) {
        this(status.toString(), message, LocalDateTime.now());
    }

}