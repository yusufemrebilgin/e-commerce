package com.example.ecommerce.cart.payload.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * A request record to update an existing cart item.
 */
public record UpdateCartItemRequest(
        @NotNull(message = "Item quantity is required. Please provide a valid quantity.")
        @Positive(message = "Item quantity must be positive number. Please enter a positive number.")
        int quantity
) {}
