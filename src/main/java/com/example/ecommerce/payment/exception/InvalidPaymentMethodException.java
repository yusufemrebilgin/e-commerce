package com.example.ecommerce.payment.exception;

import com.example.ecommerce.shared.constant.ErrorMessages;
import com.example.ecommerce.shared.exception.BadRequestException;

public class InvalidPaymentMethodException extends BadRequestException {

    public InvalidPaymentMethodException(String paymentMethod) {
        super(ErrorMessages.INVALID_PAYMENT_METHOD.message(paymentMethod));
    }

}
