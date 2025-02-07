package com.example.ecommerce.shared.constant;

public enum ErrorMessages {

    // Cart
    EMPTY_CART("Cart is empty"),
    CART_ITEM_NOT_FOUND("CartItem not found with id %s"),

    // Product
    PRODUCT_NOT_FOUND("Product not found with id %s"),
    INSUFFICIENT_STOCK("Insufficient stock capacity! Available: %d Requested: %d"),

    // Product Image
    EMPTY_FILE("File is empty %s"),
    INVALID_FILE_TYPE("Invalid file type for %s. Allowed types are %s"),
    FILE_STORAGE_FAILED("Failed to store file %s"),
    PRODUCT_IMAGE_NOT_FOUND("ProductImage not found with name %s"),

    // Order
    ORDER_NOT_FOUND("Order not found with id %s"),

    // Payment
    INVALID_PAYMENT_METHOD("Invalid payment method: %s"),
    PAYMENT_FAILED("Payment processing failed. Please try again or use a different payment method");

    private final String message;

    ErrorMessages(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }

    public String message(Object... args) {
        return String.format(message, args);
    }

}
