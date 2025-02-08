package com.example.ecommerce.payment.exception;

import com.example.ecommerce.shared.exception.InternalServerException;

public class PaymentFailedException extends InternalServerException {

    public PaymentFailedException() {
        super("Payment processing failed. Please try again or use a different payment method");
    }

}
