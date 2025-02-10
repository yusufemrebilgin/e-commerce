package com.example.ecommerce.cart.service;

import com.example.ecommerce.cart.exception.CartItemNotFoundException;
import com.example.ecommerce.cart.payload.request.CreateCartItemRequest;
import com.example.ecommerce.cart.payload.request.UpdateCartItemRequest;
import com.example.ecommerce.cart.payload.response.CartItemResponse;

/**
 * Service interface for managing operations related to cart items in the user's cart.
 */
public interface CartItemService {

    /**
     * Adds a new item to user's cart or updates the quantity of if it already exists.
     *
     * @param request a {@link CreateCartItemRequest} containing details of the item to be added
     * @return {@link CartItemResponse} containing added or updated cart item
     */
    CartItemResponse addItemToCart(CreateCartItemRequest request);

    /**
     * Updates quantity of an existing cart item in user's cart.
     *
     * @param cartItemId ID of the cart item to update
     * @param request    a {@link UpdateCartItemRequest} containing the updated quantity
     * @return {@link CartItemResponse} containing the updated cart item
     * @throws CartItemNotFoundException if cart item with given UUID is not found
     */
    CartItemResponse updateItemQuantityInCart(String cartItemId, UpdateCartItemRequest request);

    /**
     * Removes an item from a user's cart by its ID.
     *
     * @param cartItemId ID of the cart item to remove
     * @throws CartItemNotFoundException if cart item with given UUID is not found
     */
    void removeItemFromCart(String cartItemId);

}
