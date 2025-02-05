package com.example.ecommerce.order.payload.response;

import com.example.ecommerce.cart.model.embeddable.DiscountInfo;
import com.example.ecommerce.cart.model.embeddable.ProductInfo;

import java.util.UUID;

public record OrderItemResponse(
        UUID id,
        String productName,
        ProductInfo productInfo,
        DiscountInfo discountInfo
) {}
