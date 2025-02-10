package com.example.ecommerce.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * A base class for exceptions that indicate an unauthorized access attempt.
 * <p>
 * This class extends {@link ApplicationException} and uses the {@link HttpStatus#UNAUTHORIZED} status code.
 * It is intended to be used for cases where a user is not authorized to access a resource or perform an action.
 * </p>
 */
public abstract class UnauthorizedException extends ApplicationException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, HttpStatus.UNAUTHORIZED, cause);
    }

}
