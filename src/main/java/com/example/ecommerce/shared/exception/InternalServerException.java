package com.example.ecommerce.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * A base class for exceptions that indicate an internal server error.
 * <p>
 * This class extends {@link ApplicationException} and uses the {@link HttpStatus#INTERNAL_SERVER_ERROR} status code.
 * It is intended to be used for cases where the server encounters an unexpected condition that prevents it from
 * fulfilling request.
 */
public abstract class InternalServerException extends ApplicationException {

    public InternalServerException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public InternalServerException(String message, Throwable cause) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }

}
