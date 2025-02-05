package com.example.ecommerce.cart.mapper;

import com.example.ecommerce.cart.model.CartItem;
import com.example.ecommerce.cart.payload.response.CartItemResponse;
import com.example.ecommerce.product.mapper.ProductMapper;
import com.example.ecommerce.shared.mapper.Mapper;
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
