package com.example.ecommerce.cart.exception;

import com.example.ecommerce.shared.exception.BadRequestException;

public class EmptyCartException extends BadRequestException {

    public EmptyCartException() {
        super("Cart is empty");
    }

}
