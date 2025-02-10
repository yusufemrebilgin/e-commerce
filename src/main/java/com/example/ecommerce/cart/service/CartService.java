package com.example.ecommerce.cart.service;

import com.example.ecommerce.cart.model.Cart;
import com.example.ecommerce.cart.payload.response.CartResponse;
import com.example.ecommerce.cart.payload.response.CartSummaryResponse;

import java.math.BigDecimal;

/**
 * Service interface for managing the shopping cart.
 * <p>
 * Each cart is linked to an authenticated user, ensuring that users can only access and modify their own carts.
 * If a user does not have an existing cart, a new one is automatically created upon access.
 */
public interface CartService {

    /**
     * Retrieves the cart associated with the authenticated user.
     * If no cart exists, a new one is created.
     *
     * @return the {@link Cart} of the authenticated user
     */
    Cart getCartByAuthenticatedUser();

    /**
     * Retrieves a summary of the user's cart, including total item count and total price.
     *
     * @return a {@link CartResponse} representing the cart details
     */
    CartResponse getCart();

    /**
     * Retrieves a summary of the authenticated user's cart.
     *
     * @return a {@link CartSummaryResponse} containing cart ID, item count, and total price
     */
    CartSummaryResponse getCartSummary();

    /**
     * Updates the total price of the authenticated user's cart.
     *
     * @param priceDifference the amount to adjust the total price by (positive or negative)
     */
    void updateCartTotalPrice(BigDecimal priceDifference);

    /**
     * Clear all items from the user's cart and resets the total price to zero.
     */
    void clearCart();

}
