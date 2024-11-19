package com.example.ecommerce.model.enums;

public enum PaymentStatus {

    /**
     * The payment is still pending and has not yet been completed.
     */
    PENDING,

    /**
     * The payment was completed successfully.
     */
    SUCCESS,

    /**
     * The payment failed to complete.
     */
    FAILED

}
