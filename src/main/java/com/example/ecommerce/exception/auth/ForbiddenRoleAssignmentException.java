package com.example.ecommerce.exception.auth;

import com.example.ecommerce.constant.ErrorMessages;
import com.example.ecommerce.exception.ForbiddenException;

public class ForbiddenRoleAssignmentException extends ForbiddenException {

    public ForbiddenRoleAssignmentException() {
        super(ErrorMessages.FORBIDDEN_ROLE_ASSIGNMENT.message());
    }

}
