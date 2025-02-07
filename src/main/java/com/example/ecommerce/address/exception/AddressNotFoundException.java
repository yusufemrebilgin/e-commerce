package com.example.ecommerce.address.exception;

import com.example.ecommerce.shared.exception.NotFoundException;

public class AddressNotFoundException extends NotFoundException {

    public AddressNotFoundException(String message) {
        super(message);
    }

    public AddressNotFoundException(Long addressId) {
        super("Address not found with id " + addressId);
    }

}
