package com.example.ecommerce.cart.payload.response;

import com.example.ecommerce.cart.model.embeddable.DiscountInfo;
import com.example.ecommerce.cart.model.embeddable.ProductInfo;
import com.example.ecommerce.product.payload.response.ProductResponse;

public record CartItemResponse(
        String id,
        ProductResponse product,
        ProductInfo productInfo,
        DiscountInfo discountInfo
) {}
