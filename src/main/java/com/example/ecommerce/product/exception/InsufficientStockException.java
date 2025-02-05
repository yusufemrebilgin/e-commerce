package com.example.ecommerce.product.exception;

import com.example.ecommerce.shared.constant.ErrorMessages;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(int availableStock, int requestedQuantity) {
        super(ErrorMessages.INSUFFICIENT_STOCK.message(availableStock, requestedQuantity));
    }

}
