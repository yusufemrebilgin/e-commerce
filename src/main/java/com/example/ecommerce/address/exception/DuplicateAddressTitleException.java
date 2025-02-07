package com.example.ecommerce.address.exception;

import com.example.ecommerce.shared.exception.ConflictException;

public class DuplicateAddressTitleException extends ConflictException {

    public DuplicateAddressTitleException(String addressTitle) {
        super(String.format("Address with title %s already exists for current user", addressTitle));
    }

}
