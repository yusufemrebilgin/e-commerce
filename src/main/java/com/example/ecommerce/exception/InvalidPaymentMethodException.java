package com.example.ecommerce.exception;

public class InvalidPaymentMethodException extends RuntimeException {

    public InvalidPaymentMethodException(String paymentMethod) {
        super("Invalid payment method: " + paymentMethod);
    }

}
