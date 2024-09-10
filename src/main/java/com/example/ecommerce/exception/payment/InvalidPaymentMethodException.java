package com.example.ecommerce.exception.payment;

import com.example.ecommerce.constant.ErrorMessages;

public class InvalidPaymentMethodException extends RuntimeException {

    public InvalidPaymentMethodException(String paymentMethod) {
        super(ErrorMessages.INVALID_PAYMENT_METHOD.message(paymentMethod));
    }

}
