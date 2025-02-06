package com.example.ecommerce.auth.exception;

import com.example.ecommerce.shared.exception.ForbiddenException;

public class ForbiddenRoleAssignmentException extends ForbiddenException {

    public ForbiddenRoleAssignmentException(String message) {
        super(message);
    }

}
