package com.example.ecommerce.mapper;

import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.payload.dto.OrderItemDto;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper implements Mapper<OrderItem, OrderItemDto> {

    @Override
    public OrderItemDto mapToResponse(@NonNull OrderItem orderItem) {
        return new OrderItemDto(
                orderItem.getId(),
                orderItem.getProduct().getName(),
                orderItem.getQuantity(),
                orderItem.getTotalPrice(),
                orderItem.getTotalDiscountAmount()
        );
    }

}
