package com.example.ecommerce.payload.response;

import com.example.ecommerce.model.embeddable.DiscountInfo;
import com.example.ecommerce.model.embeddable.ProductInfo;

import java.util.UUID;

public record OrderItemResponse(
        UUID id,
        String productName,
        ProductInfo productInfo,
        DiscountInfo discountInfo
) {}
