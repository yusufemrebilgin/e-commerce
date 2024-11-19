package com.example.ecommerce.payload.request.category;

import jakarta.validation.constraints.NotBlank;

/**
 * A request record to create a new category.
 */
public record CreateCategoryRequest(
        @NotBlank(message = "Category name is required")
        String name
) {}
