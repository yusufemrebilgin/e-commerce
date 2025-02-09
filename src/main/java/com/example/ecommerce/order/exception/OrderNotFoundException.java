package com.example.ecommerce.order.exception;

import com.example.ecommerce.shared.exception.NotFoundException;

public class OrderNotFoundException extends NotFoundException {

    public OrderNotFoundException(String orderId) {
        super("Order not found with id '" + orderId + "'");
    }

}
