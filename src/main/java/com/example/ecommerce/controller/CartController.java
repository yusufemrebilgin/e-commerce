package com.example.ecommerce.controller;

import com.example.ecommerce.payload.dto.CartItemDto;
import com.example.ecommerce.payload.request.cart.CreateCartItemRequest;
import com.example.ecommerce.payload.request.cart.UpdateCartItemRequest;
import com.example.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<?> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @PostMapping("/items")
    public ResponseEntity<CartItemDto> addCartItem(@Valid @RequestBody CreateCartItemRequest request) {
        CartItemDto cartItem = cartService.addItemToCart(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItem);
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartItemDto> updateCartItem(@PathVariable UUID cartItemId,
                                                      @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateItemInCart(cartItemId, request));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<?> deleteCartItem(@PathVariable UUID cartItemId) {
        cartService.deleteItemFromCart(cartItemId);
        return ResponseEntity.noContent().build();
    }

}
