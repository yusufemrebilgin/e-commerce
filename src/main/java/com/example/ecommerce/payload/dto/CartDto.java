package com.example.ecommerce.payload.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record CartDto(
        Long id,
        BigDecimal totalPrice,
        List<CartItemDto> items
) {}
