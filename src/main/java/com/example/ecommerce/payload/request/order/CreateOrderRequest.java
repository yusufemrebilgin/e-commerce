package com.example.ecommerce.payload.request.order;

import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(

        @NotNull(message = "Address id must not be null")
        Long addressId,

        @NotNull(message = "Payment method must not be null")
        String paymentMethod

) {}
