package com.example.ecommerce.payment.exception;

import com.example.ecommerce.shared.exception.BadRequestException;

public class InvalidPaymentMethodException extends BadRequestException {

    public InvalidPaymentMethodException(String paymentMethod) {
        super("Invalid payment method: " + paymentMethod);
    }

}
