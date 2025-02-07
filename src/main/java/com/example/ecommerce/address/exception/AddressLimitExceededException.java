package com.example.ecommerce.address.exception;

import com.example.ecommerce.shared.exception.BadRequestException;

public class AddressLimitExceededException extends BadRequestException {

    public AddressLimitExceededException() {
        super("Cannot add more addresses");
    }

}
