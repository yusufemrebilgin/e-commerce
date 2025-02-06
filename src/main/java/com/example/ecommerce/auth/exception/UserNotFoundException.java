package com.example.ecommerce.auth.exception;

import com.example.ecommerce.shared.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(String username) {
        super("User not found with username '" + username + "'");
    }

}
