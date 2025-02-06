package com.example.ecommerce.auth.exception;

import com.example.ecommerce.shared.exception.UnauthorizedException;

public class TokenRevokedException extends UnauthorizedException {

    public TokenRevokedException() {
        super("Token is revoked");
    }

}
