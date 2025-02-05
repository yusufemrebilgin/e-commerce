package com.example.ecommerce.order.mapper;

import com.example.ecommerce.order.model.OrderItem;
import com.example.ecommerce.order.payload.response.OrderItemResponse;
import com.example.ecommerce.shared.mapper.Mapper;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper implements Mapper<OrderItem, OrderItemResponse> {

    @Override
    public OrderItemResponse mapToResponse(@NonNull OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getId(),
                orderItem.getProduct().getName(),
                orderItem.getProductInfo(),
                orderItem.getDiscountInfo()
        );
    }

}
