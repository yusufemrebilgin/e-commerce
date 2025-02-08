package com.example.ecommerce.cart.mapper;

import com.example.ecommerce.cart.model.Cart;
import com.example.ecommerce.cart.payload.response.CartResponse;
import com.example.ecommerce.shared.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CartItemMapper.class)
public interface CartMapper extends GenericMapper<Cart, CartResponse> {

    @Override
    @Mapping(target = "cartId", source = "id")
    @Mapping(target = "items", source = "cartItems")
    CartResponse mapToResponse(Cart cart);

}
