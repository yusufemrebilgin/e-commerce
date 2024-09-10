package com.example.ecommerce.exception.address;

import com.example.ecommerce.constant.ErrorMessages;

public class DuplicateAddressTitleException extends RuntimeException {

    public DuplicateAddressTitleException(String addressTitle) {
        super(ErrorMessages.DUPLICATE_ADDRESS_TITLE.format(addressTitle));
    }

}
