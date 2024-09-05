package com.example.ecommerce.exception;

public class ForbiddenRoleAssignmentException extends RuntimeException {

    public ForbiddenRoleAssignmentException(String message) {
        super(message);
    }

}
