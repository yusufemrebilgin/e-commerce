package com.example.ecommerce.order.payload.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderStatus,
        LocalDateTime orderDate,
        BigDecimal totalPrice,
        List<OrderItemResponse> items
) {}
