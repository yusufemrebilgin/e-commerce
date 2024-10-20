package com.example.ecommerce.exception.auth;

import com.example.ecommerce.constant.ErrorMessages;
import com.example.ecommerce.exception.ConflictException;

public class UsernameAlreadyTakenException extends ConflictException {

    public UsernameAlreadyTakenException() {
        super(ErrorMessages.USERNAME_ALREADY_TAKEN.message());
    }

}
