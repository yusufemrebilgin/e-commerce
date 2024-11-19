package com.example.ecommerce.exception.address;

import com.example.ecommerce.constant.ErrorMessages;
import com.example.ecommerce.exception.NotFoundException;

public class AddressNotFoundException extends NotFoundException {

    public AddressNotFoundException(String message) {
        super(message);
    }

    public AddressNotFoundException(Long addressId) {
        super(ErrorMessages.ADDRESS_NOT_FOUND.message(addressId));
    }

}
