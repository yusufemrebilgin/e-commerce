package com.example.ecommerce.product.payload.response;

import com.example.ecommerce.product.model.embeddable.Discount;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String category,
        String description,
        int stock,
        BigDecimal price,
        Discount discount,
        List<String> images
) {}
