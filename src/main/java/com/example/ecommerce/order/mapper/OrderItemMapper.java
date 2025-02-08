package com.example.ecommerce.order.mapper;

import com.example.ecommerce.order.model.OrderItem;
import com.example.ecommerce.order.payload.response.OrderItemResponse;
import com.example.ecommerce.shared.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper extends GenericMapper<OrderItem, OrderItemResponse> {

    @Override
    @Mapping(target = "productName", source = "product.name")
    OrderItemResponse mapToResponse(OrderItem orderItem);

}
