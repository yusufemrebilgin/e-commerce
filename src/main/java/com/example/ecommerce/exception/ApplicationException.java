package com.example.ecommerce.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base class for all application-specific exceptions in the e-commerce application.
 * <p>
 * This class extends {@code RuntimeException} and provides additional functionality for
 * associating an HTTP status with the exception.
 */
@Getter
public abstract class ApplicationException extends RuntimeException {

    private final HttpStatus status;

    public ApplicationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public ApplicationException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

}
