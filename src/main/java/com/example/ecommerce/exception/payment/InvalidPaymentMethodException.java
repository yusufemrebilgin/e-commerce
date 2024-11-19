package com.example.ecommerce.exception.payment;

import com.example.ecommerce.constant.ErrorMessages;
import com.example.ecommerce.exception.BadRequestException;

public class InvalidPaymentMethodException extends BadRequestException {

    public InvalidPaymentMethodException(String paymentMethod) {
        super(ErrorMessages.INVALID_PAYMENT_METHOD.message(paymentMethod));
    }

}
