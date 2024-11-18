package com.example.ecommerce.mapper;

import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.payload.response.OrderItemResponse;
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
