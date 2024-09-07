package com.example.ecommerce.exception.user;

import com.example.ecommerce.constant.ErrorMessages;

public class UsernameAlreadyTakenException extends RuntimeException {

    public UsernameAlreadyTakenException() {
        super(ErrorMessages.USERNAME_ALREADY_TAKEN.message());
    }

}
