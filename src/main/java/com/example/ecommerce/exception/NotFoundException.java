package com.example.ecommerce.exception;

import org.springframework.http.HttpStatus;

/**
 * A base class for exceptions that indicate a resource was not found.
 * <p>
 * This class extends {@link ApplicationException} and uses the {@link HttpStatus#NOT_FOUND} status code.
 * It is intended to be used for cases where requested resources are missing.
 */
public abstract class NotFoundException extends ApplicationException {

    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

}
