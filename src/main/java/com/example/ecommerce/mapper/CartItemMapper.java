package com.example.ecommerce.mapper;

import com.example.ecommerce.payload.dto.CartItemDto;
import com.example.ecommerce.model.CartItem;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartItemMapper implements Mapper<CartItem, CartItemDto> {

    private final ProductMapper productMapper;

    @Override
    public CartItemDto mapToResponse(@NonNull CartItem cartItem) {
        return new CartItemDto(
                cartItem.getId(),
                productMapper.mapToResponse(cartItem.getProduct()),
                cartItem.getQuantity(),
                cartItem.getTotalDiscountAmount(),
                cartItem.getTotalPrice()
        );
    }

}
