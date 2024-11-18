package com.example.ecommerce.model.enums;

import com.example.ecommerce.exception.payment.InvalidPaymentMethodException;

public enum PaymentMethod {

    DEBIT_CARD, CREDIT_CARD;

    /**
     * Converts a string to corresponding {@link PaymentMethod} enum constant.
     *
     * @param paymentMethod the name of the payment method to be converted
     * @return the {@link PaymentMethod} corresponding to the given string
     * @throws InvalidPaymentMethodException if the provided payment method is invalid or not supported
     */
    public static PaymentMethod fromString(String paymentMethod) {
        try {
            return PaymentMethod.valueOf(paymentMethod);
        } catch (IllegalArgumentException ex) {
            throw new InvalidPaymentMethodException(paymentMethod);
        }
    }
}
