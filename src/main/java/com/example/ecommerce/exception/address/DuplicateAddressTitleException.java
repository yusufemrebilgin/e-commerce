package com.example.ecommerce.exception.address;

import com.example.ecommerce.constant.ErrorMessages;
import com.example.ecommerce.exception.ConflictException;

public class DuplicateAddressTitleException extends ConflictException {

    public DuplicateAddressTitleException(String addressTitle) {
        super(ErrorMessages.DUPLICATE_ADDRESS_TITLE.message(addressTitle));
    }

}
