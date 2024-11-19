package com.example.ecommerce.exception.payment;

import com.example.ecommerce.constant.ErrorMessages;
import com.example.ecommerce.exception.InternalServerException;

public class PaymentFailedException extends InternalServerException {

    public PaymentFailedException() {
        super(ErrorMessages.PAYMENT_FAILED.message());
    }

}
