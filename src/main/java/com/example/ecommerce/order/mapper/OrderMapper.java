package com.example.ecommerce.order.mapper;

import com.example.ecommerce.order.model.Order;
import com.example.ecommerce.order.payload.response.OrderResponse;
import com.example.ecommerce.shared.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper extends GenericMapper<Order, OrderResponse> {

    @Override
    @Mapping(target = "items", source = "orderItems")
    OrderResponse mapToResponse(Order order);

}
