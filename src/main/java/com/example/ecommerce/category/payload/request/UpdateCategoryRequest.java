package com.example.ecommerce.category.payload.request;

import jakarta.validation.constraints.NotBlank;

/**
 * A request record to update an existing category.
 */
public record UpdateCategoryRequest(
        @NotBlank(message = "Category name is required")
        String name
) {}
