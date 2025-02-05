package com.example.ecommerce.auth.exception;

import com.example.ecommerce.shared.constant.ErrorMessages;
import com.example.ecommerce.shared.exception.NotFoundException;

public class RoleNotFoundException extends NotFoundException {

    public RoleNotFoundException(String roleName) {
        super(ErrorMessages.ROLE_NOT_FOUND.message(roleName));
    }

}
