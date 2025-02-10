package com.example.ecommerce.cart.payload.response;

import java.math.BigDecimal;

public record CartSummaryResponse(
        Long cartId,
        int itemCount,
        BigDecimal totalPrice
) {}
