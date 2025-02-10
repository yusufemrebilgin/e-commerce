package com.example.ecommerce.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * A base class for exceptions that indicate a forbidden action.
 * <p>
 * This class extends {@link ApplicationException} and uses the {@link HttpStatus#FORBIDDEN} status code.
 * It is intended to be used for cases where the user does not have permission to perform specific action.
 */
public abstract class ForbiddenException extends ApplicationException {

    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

}
