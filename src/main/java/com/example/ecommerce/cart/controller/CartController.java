package com.example.ecommerce.cart.controller;

import com.example.ecommerce.cart.payload.response.CartResponse;
import com.example.ecommerce.cart.payload.response.CartSummaryResponse;
import com.example.ecommerce.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    /**
     * Retrieves the current cart for the authenticated user.
     *
     * @return a {@link ResponseEntity} containing the {@link CartResponse} with cart details
     */
    @GetMapping("/current")
    public ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    /**
     * Retrieves a summary of the current cart, including total item count and total price.
     *
     * @return a {@link ResponseEntity} containing the {@link CartSummaryResponse} with cart summary details
     */
    @GetMapping("/summary")
    public ResponseEntity<CartSummaryResponse> getCartSummary() {
        return ResponseEntity.ok(cartService.getCartSummary());
    }

    /**
     * Clears all items from the current cart for the authenticated user.
     *
     * @return a {@link ResponseEntity} with no content indicating the cart has been cleared
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }

}
