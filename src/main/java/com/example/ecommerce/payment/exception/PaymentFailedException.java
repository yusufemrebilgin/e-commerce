package com.example.ecommerce.payment.exception;

import com.example.ecommerce.shared.constant.ErrorMessages;
import com.example.ecommerce.shared.exception.InternalServerException;

public class PaymentFailedException extends InternalServerException {

    public PaymentFailedException() {
        super(ErrorMessages.PAYMENT_FAILED.message());
    }

}
