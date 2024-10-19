package com.example.ecommerce.exception;

import org.springframework.http.HttpStatus;

/**
 * A base class for exceptions that indicate a conflict error.
 * <p>
 * This class extends {@link ApplicationException} and uses the {@link HttpStatus#CONFLICT} status code.
 * It is intended to be used for cases where the request could not be completed due to a conflict with the current
 * state of the resource.
 */
public abstract class ConflictException extends ApplicationException {

    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

}
