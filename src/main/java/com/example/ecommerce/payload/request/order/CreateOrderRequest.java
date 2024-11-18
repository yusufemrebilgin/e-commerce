package com.example.ecommerce.payload.request.order;

import jakarta.validation.constraints.NotNull;

/**
 * A request record to create a new order.
 */
public record CreateOrderRequest(

        @NotNull(message = "Address ID is required. Please provide a valid address ID.")
        Long addressId,

        @NotNull(message = "Payment method is required. Please provide a valid payment method.")
        String paymentMethod

) {}
