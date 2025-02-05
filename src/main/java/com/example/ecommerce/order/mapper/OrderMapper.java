package com.example.ecommerce.order.mapper;

import com.example.ecommerce.order.model.Order;
import com.example.ecommerce.order.payload.response.OrderResponse;
import com.example.ecommerce.shared.mapper.Mapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMapper implements Mapper<Order, OrderResponse> {

    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderResponse mapToResponse(@NonNull Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderStatus().name(),
                order.getOrderDate(),
                order.getTotalPrice(),
                orderItemMapper.mapToResponseList(order.getOrderItems(), orderItemMapper)
        );
    }

}
