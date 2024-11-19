package com.example.ecommerce.payload.request.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * A request record to create a new cart item.
 */
public record CreateCartItemRequest(

        @NotNull(message = "Product ID is required. Please provide a valid product ID.")
        String productId,

        @NotNull(message = "Item quantity is required. Please provide a valid quantity.")
        @Positive(message = "Item quantity must be positive number. Please enter a positive number.")
        int quantity

) {}
