package com.example.ecommerce.exception.user;

import com.example.ecommerce.constant.ErrorMessages;

public class EmailAlreadyInUseException extends RuntimeException {

    public EmailAlreadyInUseException() {
        super(ErrorMessages.EMAIL_ALREADY_IN_USE.message());
    }

}
