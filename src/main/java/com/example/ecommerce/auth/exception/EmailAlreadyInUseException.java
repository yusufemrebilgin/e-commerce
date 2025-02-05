package com.example.ecommerce.auth.exception;

import com.example.ecommerce.shared.constant.ErrorMessages;
import com.example.ecommerce.shared.exception.ConflictException;

public class EmailAlreadyInUseException extends ConflictException {

    public EmailAlreadyInUseException() {
        super(ErrorMessages.EMAIL_ALREADY_IN_USE.message());
    }

}
