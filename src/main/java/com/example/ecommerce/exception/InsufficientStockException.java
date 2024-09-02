package com.example.ecommerce.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(int availableStock, int requestedQuantity) {
        super("Insufficient stock capacity! " +
                "Available stock: " + availableStock + " " +
                "Requested Quantity: " + requestedQuantity);
    }

}
