package com.example.ecommerce.model.enums;

import com.example.ecommerce.exception.payment.InvalidPaymentMethodException;

public enum PaymentMethod {
    DEBIT_CARD,
    CREDIT_CARD;

    public static PaymentMethod fromString(String paymentMethod) {
        try {
            return PaymentMethod.valueOf(paymentMethod);
        } catch (IllegalArgumentException ex) {
            throw new InvalidPaymentMethodException(paymentMethod);
        }
    }
}
