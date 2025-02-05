package com.example.ecommerce.category.payload.request;

import jakarta.validation.constraints.NotBlank;

/**
 * A request record to create a new category.
 */
public record CreateCategoryRequest(
        @NotBlank(message = "Category name is required")
        String name
) {}
