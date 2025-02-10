package com.example.ecommerce.auth.exception;

import com.example.ecommerce.shared.exception.ConflictException;

public class UsernameAlreadyTakenException extends ConflictException {

    public UsernameAlreadyTakenException() {
        super("Username is already taken");
    }

}
