package com.example.ecommerce.exception;

import java.util.UUID;

public class CartItemNotFoundException extends NotFoundException {

    public CartItemNotFoundException(UUID id) {
        super(String.format(DEFAULT_FORMAT_WITH_ID, "CartItem", id));
    }

}
