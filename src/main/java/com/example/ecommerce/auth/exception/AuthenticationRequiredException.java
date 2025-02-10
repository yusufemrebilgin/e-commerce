package com.example.ecommerce.auth.exception;

import com.example.ecommerce.shared.exception.UnauthorizedException;

public class AuthenticationRequiredException extends UnauthorizedException {

    public AuthenticationRequiredException(String message) {
        super(message);
    }

}
