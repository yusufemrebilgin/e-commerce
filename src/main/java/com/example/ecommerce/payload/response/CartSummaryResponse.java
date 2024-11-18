package com.example.ecommerce.payload.response;

import java.math.BigDecimal;

public record CartSummaryResponse(
        Long cartId,
        int itemCount,
        BigDecimal totalPrice
) {}
