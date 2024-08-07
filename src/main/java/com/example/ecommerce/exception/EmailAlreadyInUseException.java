package com.example.ecommerce.exception;

public class EmailAlreadyInUseException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Email is already in use";

    public EmailAlreadyInUseException() {
        super(DEFAULT_MESSAGE);
    }

}
