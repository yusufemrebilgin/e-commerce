package com.example.ecommerce.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * A base class for exceptions that indicate a bad request error.
 * <p>
 * This class extends {@link ApplicationException} and uses the {@link HttpStatus#BAD_REQUEST} status code.
 * It is intended to be used for cases where the client has sent an invalid or malformed request.
 */
public abstract class BadRequestException extends ApplicationException {

    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

}
