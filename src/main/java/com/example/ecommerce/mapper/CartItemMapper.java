package com.example.ecommerce.mapper;

import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.payload.response.CartItemResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartItemMapper implements Mapper<CartItem, CartItemResponse> {

    private final ProductMapper productMapper;

    @Override
    public CartItemResponse mapToResponse(@NonNull CartItem cartItem) {
        return new CartItemResponse(
                cartItem.getId(),
                productMapper.mapToResponse(cartItem.getProduct()),
                cartItem.getProductInfo(),
                cartItem.getDiscountInfo()
        );
    }

}
