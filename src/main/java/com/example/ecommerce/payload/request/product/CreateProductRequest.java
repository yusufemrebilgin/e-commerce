package com.example.ecommerce.payload.request.product;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateProductRequest(

        @NotBlank(message = "Product name must not be blank or null")
        String name,

        @NotBlank(message = "Product description must not be blank or null")
        String description,

        @NotNull(message = "Price must not be null")
        @PositiveOrZero(message = "Price must be zero or positive")
        BigDecimal price,

        @NotNull(message = "Stock quantity must not be null")
        @Positive(message = "Stock quantity must be positive")
        int quantity,

        @Min(value = 0, message = "Discount percentage must be at least 0")
        @Max(value = 100, message = "Discount percentage must be at most 100")
        Double discountPercentage,

        @Future(message = "Discount start date must be in the future")
        LocalDateTime discountStart,

        @Future(message = "Discount start date must be in the future")
        LocalDateTime discountEnd

) {}
