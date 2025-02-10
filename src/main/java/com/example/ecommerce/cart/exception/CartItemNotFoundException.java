package com.example.ecommerce.cart.exception;

import com.example.ecommerce.shared.exception.NotFoundException;

public class CartItemNotFoundException extends NotFoundException {

    public CartItemNotFoundException(String cartId) {
        super("Cart item not found with id " + cartId);
    }

}
