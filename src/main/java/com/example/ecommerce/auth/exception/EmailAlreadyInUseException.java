package com.example.ecommerce.auth.exception;

import com.example.ecommerce.shared.exception.ConflictException;

public class EmailAlreadyInUseException extends ConflictException {

    public EmailAlreadyInUseException() {
        super("Email is already in use");
    }

}
