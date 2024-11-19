package com.example.ecommerce.exception.cart;

import com.example.ecommerce.constant.ErrorMessages;
import com.example.ecommerce.exception.NotFoundException;

import java.util.UUID;

public class CartItemNotFoundException extends NotFoundException {

    public CartItemNotFoundException(UUID cartId) {
        super(ErrorMessages.CART_ITEM_NOT_FOUND.message(cartId));
    }

}
