package com.example.ecommerce.order.payload.response;

import com.example.ecommerce.cart.model.embeddable.DiscountInfo;
import com.example.ecommerce.cart.model.embeddable.ProductInfo;

public record OrderItemResponse(
        String id,
        String productName,
        ProductInfo productInfo,
        DiscountInfo discountInfo
) {}
