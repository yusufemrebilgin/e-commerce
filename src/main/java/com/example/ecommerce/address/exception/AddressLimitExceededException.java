package com.example.ecommerce.address.exception;

import com.example.ecommerce.shared.constant.ErrorMessages;
import com.example.ecommerce.shared.exception.BadRequestException;

public class AddressLimitExceededException extends BadRequestException {

    public AddressLimitExceededException() {
        super(ErrorMessages.ADDRESS_LIMIT_EXCEEDED.message());
    }

}
