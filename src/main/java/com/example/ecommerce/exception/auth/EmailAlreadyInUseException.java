package com.example.ecommerce.exception.auth;

import com.example.ecommerce.constant.ErrorMessages;
import com.example.ecommerce.exception.ConflictException;

public class EmailAlreadyInUseException extends ConflictException {

    public EmailAlreadyInUseException() {
        super(ErrorMessages.EMAIL_ALREADY_IN_USE.message());
    }

}
