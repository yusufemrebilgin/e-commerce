package com.example.ecommerce.model.enums;

import com.example.ecommerce.exception.payment.InvalidPaymentMethodException;

public enum PaymentMethod {
    DEBIT_CART,
    CREDIT_CART;

    public static PaymentMethod fromString(String paymentMethod) {
        try {
            return PaymentMethod.valueOf(paymentMethod);
        } catch (IllegalArgumentException ex) {
            throw new InvalidPaymentMethodException(paymentMethod);
        }
    }
}
