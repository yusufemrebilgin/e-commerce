package com.example.ecommerce.exception;

public class EmptyCartException extends RuntimeException {

    public EmptyCartException() {
        super("Cart is empty");
    }

}
