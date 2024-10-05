package com.example.ecommerce.mapper;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.payload.dto.OrderDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMapper implements Mapper<Order, OrderDto> {

    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderDto mapToResponse(@NonNull Order order) {
        return new OrderDto(
                order.getId(),
                order.getOrderStatus().name(),
                order.getOrderDate(),
                order.getTotalPrice(),
                orderItemMapper.mapToResponseList(order.getOrderItems(), orderItemMapper)
        );
    }

}
