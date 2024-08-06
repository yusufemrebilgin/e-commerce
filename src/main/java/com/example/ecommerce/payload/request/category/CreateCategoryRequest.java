package com.example.ecommerce.payload.request.category;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(
        @NotBlank(message = "Category name must not be blank or null")
        String categoryName
) {}
