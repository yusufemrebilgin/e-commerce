package com.example.ecommerce.cart.payload.response;

import com.example.ecommerce.cart.model.embeddable.DiscountInfo;
import com.example.ecommerce.cart.model.embeddable.ProductInfo;
import com.example.ecommerce.product.payload.response.ProductResponse;

import java.util.UUID;

public record CartItemResponse(
        UUID id,
        ProductResponse product,
        ProductInfo productInfo,
        DiscountInfo discountInfo
) {}
