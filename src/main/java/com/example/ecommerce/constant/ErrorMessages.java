package com.example.ecommerce.constant;

public enum ErrorMessages {

    // User
    ROLE_NOT_FOUND("Role not found with name %s"),
    EMAIL_ALREADY_IN_USE("Email is already in use"),
    USERNAME_ALREADY_TAKEN("Username is already taken"),
    FORBIDDEN_ROLE_ASSIGNMENT("Only SUPER_ADMIN can assign ADMIN or SUPER_ADMIN role"),

    // Cart
    EMPTY_CART("Cart is empty"),
    CART_ITEM_NOT_FOUND("CartItem not found with id %s"),

    // Category
    CATEGORY_NOT_FOUND("Category not found with id %s"),

    // Product
    PRODUCT_NOT_FOUND("Product not found with id %s"),
    PRODUCT_IMAGE_NOT_FOUND("ProductImage not found with name %s"),
    INSUFFICIENT_STOCK("Insufficient stock capacity! Available: %d Requested: %d"),

    // Address
    ADDRESS_NOT_FOUND("Address not found with id %s"),
    ADDRESS_LIMIT_EXCEEDED("Cannot add more addresses"),

    // Payment
    INVALID_PAYMENT_METHOD("Invalid payment method: %s");

    private final String message;

    ErrorMessages(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }

}
