package com.example.ecommerce.order.exception;

import com.example.ecommerce.shared.exception.NotFoundException;

public class OrderNotFoundException extends NotFoundException {

    public OrderNotFoundException(Long orderId) {
        super("Order not found with id " + orderId);
    }

}
