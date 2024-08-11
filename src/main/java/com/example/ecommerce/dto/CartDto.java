package com.example.ecommerce.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartDto(
        Long id,
        BigDecimal totalPrice,
        List<CartItemDto> items
) {}
