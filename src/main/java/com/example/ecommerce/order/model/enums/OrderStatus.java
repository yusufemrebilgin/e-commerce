package com.example.ecommerce.order.model.enums;

public enum OrderStatus {

    /**
     * The order has been created but the payment has not yet been processed.
     */
    PENDING,

    /**
     * The payment has been processed and the order is being prepared.
     */
    PROCESSING,

    /**
     * The order has been successfully completed.
     */
    COMPLETED,

    /**
     * The order has been cancelled before or during processing.
     */
    CANCELLED,

    /**
     * The order has failed due to payment or other issues.
     */
    FAILED

}
