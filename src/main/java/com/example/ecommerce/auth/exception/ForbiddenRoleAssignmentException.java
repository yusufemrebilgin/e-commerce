package com.example.ecommerce.auth.exception;

import com.example.ecommerce.shared.constant.ErrorMessages;
import com.example.ecommerce.shared.exception.ForbiddenException;

public class ForbiddenRoleAssignmentException extends ForbiddenException {

    public ForbiddenRoleAssignmentException() {
        super(ErrorMessages.FORBIDDEN_ROLE_ASSIGNMENT.message());
    }

}
