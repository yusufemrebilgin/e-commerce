package com.example.ecommerce.payload.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CartItemDto(
        UUID id,
        ProductDto product,
        int quantity,
        BigDecimal discount,
        BigDecimal totalPrice
) {}
