package com.example.ecommerce.address.exception;

import com.example.ecommerce.shared.constant.ErrorMessages;
import com.example.ecommerce.shared.exception.ConflictException;

public class DuplicateAddressTitleException extends ConflictException {

    public DuplicateAddressTitleException(String addressTitle) {
        super(ErrorMessages.DUPLICATE_ADDRESS_TITLE.message(addressTitle));
    }

}
