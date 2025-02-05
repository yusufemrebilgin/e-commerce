package com.example.ecommerce.cart.payload.response;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long cartId,
        BigDecimal totalPrice,
        List<CartItemResponse> items
) {}
