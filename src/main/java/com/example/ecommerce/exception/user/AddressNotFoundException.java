package com.example.ecommerce.exception.user;

import com.example.ecommerce.constant.ErrorMessages;
import com.example.ecommerce.exception.NotFoundException;

public class AddressNotFoundException extends NotFoundException {

    public AddressNotFoundException(String message) {
        super(message);
    }

    public AddressNotFoundException(Long id) {
        super(ErrorMessages.ADDRESS_NOT_FOUND.format(id));
    }

}
