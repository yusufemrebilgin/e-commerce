package com.example.ecommerce.product.payload.response;

import com.example.ecommerce.product.model.embeddable.Discount;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        String id,
        String name,
        String category,
        String description,
        int stock,
        BigDecimal price,
        Discount discount,
        List<String> images
) {}
