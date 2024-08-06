package com.example.ecommerce.exception;

public class NotFoundException extends RuntimeException {

    protected static final String DEFAULT_FORMAT = "%s not found";
    protected static final String DEFAULT_FORMAT_WITH_ID = DEFAULT_FORMAT + " with id %s";
    protected static final String DEFAULT_FORMAT_WITH_NAME = DEFAULT_FORMAT + " with name %s";

    public NotFoundException(String message) {
        super(message);
    }

}
