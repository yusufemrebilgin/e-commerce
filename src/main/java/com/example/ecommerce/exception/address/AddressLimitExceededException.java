package com.example.ecommerce.exception.address;

import com.example.ecommerce.constant.ErrorMessages;
import com.example.ecommerce.exception.BadRequestException;

public class AddressLimitExceededException extends BadRequestException {

    public AddressLimitExceededException() {
        super(ErrorMessages.ADDRESS_LIMIT_EXCEEDED.message());
    }

}
