package com.example.ecommerce.exception.user;

import com.example.ecommerce.constant.ErrorMessages;

public class ForbiddenRoleAssignmentException extends RuntimeException {

    public ForbiddenRoleAssignmentException() {
        super(ErrorMessages.FORBIDDEN_ROLE_ASSIGNMENT.message());
    }

}
