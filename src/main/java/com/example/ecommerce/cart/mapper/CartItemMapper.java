package com.example.ecommerce.cart.mapper;

import com.example.ecommerce.cart.model.CartItem;
import com.example.ecommerce.cart.payload.response.CartItemResponse;
import com.example.ecommerce.product.mapper.ProductMapper;
import com.example.ecommerce.shared.mapper.GenericMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface CartItemMapper extends GenericMapper<CartItem, CartItemResponse> {

    @Override
    CartItemResponse mapToResponse(CartItem cartItem);

}
