package com.example.ecommerce.cart.exception;

import com.example.ecommerce.shared.constant.ErrorMessages;
import com.example.ecommerce.shared.exception.BadRequestException;

public class EmptyCartException extends BadRequestException {

    public EmptyCartException() {
        super(ErrorMessages.EMPTY_CART.message());
    }

}
