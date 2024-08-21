package com.example.ecommerce.exception;

public class UnauthorizedRoleAssignmentException extends RuntimeException {

    public UnauthorizedRoleAssignmentException(String message) {
        super(message);
    }

}
