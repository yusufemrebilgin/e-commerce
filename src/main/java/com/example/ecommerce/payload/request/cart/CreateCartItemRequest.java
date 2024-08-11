package com.example.ecommerce.payload.request.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateCartItemRequest(

        @NotNull(message = "Product id must not be null")
        String productId,

        @NotNull(message = "Item quantity must not be null")
        @Positive(message = "Item quantity must be positive")
        int quantity

) {}
