package com.example.ecommerce.address.exception;

import com.example.ecommerce.shared.constant.ErrorMessages;
import com.example.ecommerce.shared.exception.NotFoundException;

public class AddressNotFoundException extends NotFoundException {

    public AddressNotFoundException(String message) {
        super(message);
    }

    public AddressNotFoundException(Long addressId) {
        super(ErrorMessages.ADDRESS_NOT_FOUND.message(addressId));
    }

}
