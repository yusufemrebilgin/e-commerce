package com.example.ecommerce.payload.request.category;

import jakarta.validation.constraints.NotBlank;

/**
 * A request record to update an existing category.
 */
public record UpdateCategoryRequest(
        @NotBlank(message = "Category name is required")
        String categoryName
) {}
