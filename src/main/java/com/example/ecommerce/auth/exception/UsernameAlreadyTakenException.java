package com.example.ecommerce.auth.exception;

import com.example.ecommerce.shared.constant.ErrorMessages;
import com.example.ecommerce.shared.exception.ConflictException;

public class UsernameAlreadyTakenException extends ConflictException {

    public UsernameAlreadyTakenException() {
        super(ErrorMessages.USERNAME_ALREADY_TAKEN.message());
    }

}
