package com.example.ecommerce.exception;

public class RoleNotFoundException extends NotFoundException {

    public RoleNotFoundException() {
        super(String.format(DEFAULT_FORMAT, "Role"));
    }

    public RoleNotFoundException(String roleName) {
        super(String.format(DEFAULT_FORMAT_WITH_NAME, "Role", roleName));
    }

}
