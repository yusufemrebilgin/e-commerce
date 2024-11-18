package com.example.ecommerce.exception.cart;

import com.example.ecommerce.constant.ErrorMessages;
import com.example.ecommerce.exception.BadRequestException;

public class EmptyCartException extends BadRequestException {

    public EmptyCartException() {
        super(ErrorMessages.EMPTY_CART.message());
    }

}
