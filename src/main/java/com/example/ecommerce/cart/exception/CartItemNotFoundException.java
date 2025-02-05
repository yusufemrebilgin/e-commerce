package com.example.ecommerce.cart.exception;

import com.example.ecommerce.shared.constant.ErrorMessages;
import com.example.ecommerce.shared.exception.NotFoundException;

import java.util.UUID;

public class CartItemNotFoundException extends NotFoundException {

    public CartItemNotFoundException(UUID cartId) {
        super(ErrorMessages.CART_ITEM_NOT_FOUND.message(cartId));
    }

}
