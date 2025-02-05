package com.example.ecommerce.order.exception;

import com.example.ecommerce.shared.constant.ErrorMessages;
import com.example.ecommerce.shared.exception.NotFoundException;

public class OrderNotFoundException extends NotFoundException {

    public OrderNotFoundException(Long orderId) {
        super(ErrorMessages.ORDER_NOT_FOUND.message(orderId));
    }

}
