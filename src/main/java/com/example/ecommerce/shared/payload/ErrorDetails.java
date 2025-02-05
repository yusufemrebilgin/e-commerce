package com.example.ecommerce.shared.payload;

import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents detailed error information that is sent to the client when validation or other errors
 * occur. This class encapsulates the HTTP status, error message, timestamp of the error occurrence,
 * and any specific validation errors.
 * <p>
 * The {@link ErrorDetails} is used to provide comprehensive error responses that include:
 *
 * @param status    The HTTP status code or description as a string
 * @param message   A detailed message describing the error
 * @param timestamp The timestamp when the error occurred
 * @param errors    A map containing specific validation errors
 */
public record ErrorDetails(
        String status,
        String message,
        LocalDateTime timestamp,
        Map<String, String> errors
) {

    public ErrorDetails(HttpStatusCode status, String message, Map<String, String> errors) {
        this(status.toString(), message, LocalDateTime.now(), errors != null ? errors : new HashMap<>());
    }

}