package com.example.ecommerce.exception.order;

import com.example.ecommerce.constant.ErrorMessages;
import com.example.ecommerce.exception.NotFoundException;

public class OrderNotFoundException extends NotFoundException {

    public OrderNotFoundException(Long orderId) {
        super(ErrorMessages.ORDER_NOT_FOUND.message(orderId));
    }

}
