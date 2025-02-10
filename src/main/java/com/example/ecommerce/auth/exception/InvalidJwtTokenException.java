package com.example.ecommerce.auth.exception;

import com.example.ecommerce.shared.exception.UnauthorizedException;

public class InvalidJwtTokenException extends UnauthorizedException {

    public InvalidJwtTokenException(String message, Throwable cause) {
        super(message, cause);
    }

}
