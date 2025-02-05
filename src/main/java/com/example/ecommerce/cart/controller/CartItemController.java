package com.example.ecommerce.cart.controller;

import com.example.ecommerce.cart.service.CartItemService;
import com.example.ecommerce.cart.payload.response.CartItemResponse;
import com.example.ecommerce.cart.payload.request.CreateCartItemRequest;
import com.example.ecommerce.cart.payload.request.UpdateCartItemRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@RequestMapping("/api/v1/cart/items")
public class CartItemController {

    private final CartItemService cartItemService;

    /**
     * Adds a new item to the cart for the authenticated user.
     *
     * @param request the {@link CreateCartItemRequest} containing item details to be added
     * @return a {@link ResponseEntity} containing the {@link CartItemResponse} of the added item
     */
    @PostMapping
    public ResponseEntity<CartItemResponse> addItemToCart(@Valid @RequestBody CreateCartItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemService.addItemToCart(request));
    }

    /**
     * Updates the quantity of an existing item in the cart.
     *
     * @param itemId  the unique identifier of the item to be updated
     * @param request the {@link UpdateCartItemRequest} containing the new quantity
     * @return a {@link ResponseEntity} containing the updated {@link CartItemResponse}
     */
    @PutMapping("/{itemId}")
    public ResponseEntity<CartItemResponse> updateItemQuantity(@PathVariable UUID itemId, @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartItemService.updateItemQuantityInCart(itemId, request));
    }

    /**
     * Removes an item from the cart for the authenticated user.
     *
     * @param itemId the unique identifier of the item to be removed
     * @return a {@link ResponseEntity} with no content indicating the item has been removed
     */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable UUID itemId) {
        cartItemService.removeItemFromCart(itemId);
        return ResponseEntity.noContent().build();
    }

}
