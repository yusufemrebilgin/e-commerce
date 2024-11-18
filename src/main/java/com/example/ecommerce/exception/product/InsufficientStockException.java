package com.example.ecommerce.exception.product;

import com.example.ecommerce.constant.ErrorMessages;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(int availableStock, int requestedQuantity) {
        super(ErrorMessages.INSUFFICIENT_STOCK.message(availableStock, requestedQuantity));
    }

}
