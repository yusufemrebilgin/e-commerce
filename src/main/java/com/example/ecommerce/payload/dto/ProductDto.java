package com.example.ecommerce.payload.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductDto(
        UUID id,
        String name,
        String category,
        String description,
        int quantity,
        BigDecimal pricePerUnit,
        BigDecimal discountedPrice,
        boolean hasDiscount,
        Double discountPercentage,
        LocalDateTime discountStart,
        LocalDateTime discountEnd,
        Set<String> imageUrls
) {}
