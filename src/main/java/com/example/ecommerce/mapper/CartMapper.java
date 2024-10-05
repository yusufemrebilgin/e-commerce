package com.example.ecommerce.mapper;

import com.example.ecommerce.payload.dto.CartDto;
import com.example.ecommerce.model.Cart;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartMapper implements Mapper<Cart, CartDto> {

    private final CartItemMapper cartItemMapper;

    @Override
    public CartDto mapToResponse(@NonNull Cart cart) {
        return new CartDto(
                cart.getId(),
                cart.getTotalPrice(),
                cartItemMapper.mapToResponseList(cart.getCartItems(), cartItemMapper)
        );
    }

}
