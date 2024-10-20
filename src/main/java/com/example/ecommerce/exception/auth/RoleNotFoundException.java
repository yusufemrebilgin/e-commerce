package com.example.ecommerce.exception.auth;

import com.example.ecommerce.constant.ErrorMessages;
import com.example.ecommerce.exception.NotFoundException;

public class RoleNotFoundException extends NotFoundException {

    public RoleNotFoundException(String roleName) {
        super(ErrorMessages.ROLE_NOT_FOUND.message(roleName));
    }

}
