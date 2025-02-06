package com.example.ecommerce.shared.payload;

import com.example.ecommerce.shared.exception.ApplicationException;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;

/**
 * A record representing an error response to be returned in API responses.
 * Contains information about the error status, title, details, path, and timestamp.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        Integer status,
        String title,
        String detail,
        String path,
        String timestamp
) {

    public static ErrorResponse of(ApplicationException exception, WebRequest request) {
        return new ErrorResponse(
                exception.getStatus().value(),
                exception.getStatus().getReasonPhrase(),
                exception.getMessage(),
                request.getDescription(false),
                Instant.now().toString()
        );
    }

    public static ErrorResponse of(Exception exception, HttpStatus status, String requestPath) {
        return new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                requestPath,
                Instant.now().toString()
        );
    }

}
