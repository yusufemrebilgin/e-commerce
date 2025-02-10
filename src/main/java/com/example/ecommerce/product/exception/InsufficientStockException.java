package com.example.ecommerce.product.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(int availableStock, int requestedQuantity) {
        super(String.format(
                "Insufficient stock capacity! Available: %d Requested: %d",
                availableStock, requestedQuantity
        ));
    }

}
