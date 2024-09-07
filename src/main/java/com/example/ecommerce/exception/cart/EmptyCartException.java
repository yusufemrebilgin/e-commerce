package com.example.ecommerce.exception.cart;

import com.example.ecommerce.constant.ErrorMessages;

public class EmptyCartException extends RuntimeException {

    public EmptyCartException() {
        super(ErrorMessages.EMPTY_CART.message());
    }

}
