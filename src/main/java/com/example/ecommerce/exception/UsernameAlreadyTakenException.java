package com.example.ecommerce.exception;

public class UsernameAlreadyTakenException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Username is already taken";

    public UsernameAlreadyTakenException() {
        super(DEFAULT_MESSAGE);
    }

}
