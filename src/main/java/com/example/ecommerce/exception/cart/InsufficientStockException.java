package com.example.ecommerce.exception.cart;

import com.example.ecommerce.constant.ErrorMessages;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(int availableStock, int requestedQuantity) {
        super(ErrorMessages.INSUFFICIENT_STOCK.message(availableStock, requestedQuantity));
    }

}
