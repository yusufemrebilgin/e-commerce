package com.example.ecommerce.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record ProductDto(
        UUID id,
        String name,
        String category,
        String description,
        int quantity,
        BigDecimal price,
        BigDecimal discountedPrice,
        Double discountPercentage,
        LocalDateTime discountStart,
        LocalDateTime discountEnd,
        Set<String> imageUrls
) {}
