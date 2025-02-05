package com.example.ecommerce.product.payload.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A request record to update an existing product.
 */
public record UpdateProductRequest(

        @NotBlank(message = "Product name cannot be blank. Please provide a valid product name.")
        String name,

        @NotBlank(message = "Product description cannot be blank. Please provide a description.")
        String description,

        @NotNull(message = "Category ID is required. Please select a category for the product.")
        Long categoryId,

        @NotNull(message = "Stock quantity is required. Please provide the quantity in the stock.")
        @Positive(message = "Stock quantity must be a positive number greater than zero.")
        int stock,

        @NotNull(message = "Price is required. Please specify the product price.")
        @PositiveOrZero(message = "Price must be zero or a positive number.")
        BigDecimal price,

        @Min(value = 0, message = "Discount percentage must be at least 0%.")
        @Max(value = 100, message = "Discount percentage cannot exceed 100%.")
        Double discountPercentage,

        @Future(message = "Discount start date must be in the future. Please choose a valid start date.")
        LocalDateTime discountStart,

        @Future(message = "Discount start date must be in the future. Please choose a valid end date.")
        LocalDateTime discountEnd

) {}
