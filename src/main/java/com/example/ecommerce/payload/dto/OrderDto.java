package com.example.ecommerce.payload.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderDto(
        Long id,
        String orderStatus,
        LocalDateTime orderDate,
        BigDecimal totalPrice,
        List<OrderItemDto> items
) {}
