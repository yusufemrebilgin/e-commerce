package com.example.ecommerce.exception.address;

import com.example.ecommerce.constant.ErrorMessages;

public class AddressLimitExceededException extends RuntimeException {

    public AddressLimitExceededException() {
        super(ErrorMessages.ADDRESS_LIMIT_EXCEEDED.message());
    }

}
