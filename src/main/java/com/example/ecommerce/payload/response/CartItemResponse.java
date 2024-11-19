package com.example.ecommerce.payload.response;

import com.example.ecommerce.model.embeddable.DiscountInfo;
import com.example.ecommerce.model.embeddable.ProductInfo;

import java.util.UUID;

public record CartItemResponse(
        UUID id,
        ProductResponse product,
        ProductInfo productInfo,
        DiscountInfo discountInfo
) {}
