package com.example.ecommerce.order.payload.request;

/**
 * A request record to cancel an existing order.
 */
public record CancelOrderRequest(
        String orderId
) {}
