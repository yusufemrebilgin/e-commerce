package com.example.ecommerce.mapper;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.payload.response.CartResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartMapper implements Mapper<Cart, CartResponse> {

    private final CartItemMapper cartItemMapper;

    @Override
    public CartResponse mapToResponse(@NonNull Cart cart) {
        return new CartResponse(
                cart.getId(),
                cart.getTotalPrice(),
                cartItemMapper.mapToResponseList(cart.getCartItems(), cartItemMapper)
        );
    }

}
