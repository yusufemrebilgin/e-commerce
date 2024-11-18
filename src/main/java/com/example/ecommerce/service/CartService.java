package com.example.ecommerce.service;

import com.example.ecommerce.mapper.CartMapper;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.payload.response.CartResponse;
import com.example.ecommerce.payload.response.CartSummaryResponse;
import com.example.ecommerce.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final AuthService authService;

    private final CartMapper cartMapper;
    private final CartRepository cartRepository;

    /**
     * Retrieves the authenticated user's username.
     * <p>
     * Although this method is already available in {@code AuthService},
     * it is defined in {@code CartService} to assist other services like
     * {@code OrderService} in accessing the authenticated user's information
     * without needing to directly inject {@code AuthService}.
     *
     * @return authenticated user's username
     */
    String getAuthenticatedUsername() {
        return authService.getCurrentUsername();
    }

    /**
     * Retrieves cart for the authenticated user. If no cart exists, a new one is created.
     *
     * @return user's cart
     * @throws IllegalStateException     if user is not authenticated (by AuthService)
     * @throws UsernameNotFoundException if user is not found by specified username (by AuthService)
     */
    protected Cart getCartByAuthenticatedUser() {
        return cartRepository
                .findByUser(authService.getCurrentUsername())
                .orElseGet(this::createCart);
    }

    /**
     * Retrieves complete cart details for the authenticated user as a {@code CartResponse} object.
     *
     * @return a {@link CartResponse} containing cart details
     */
    public CartResponse getCart() {
        return cartMapper.mapToResponse(getCartByAuthenticatedUser());
    }

    /**
     * Retrieves a summary of the user's cart, including total item count and total price.
     *
     * @return a {@link CartSummaryResponse} containing cart summary details
     */
    public CartSummaryResponse getCartSummary() {
        Cart cart = getCartByAuthenticatedUser();
        int itemCount = cart.getCartItems().stream().mapToInt(CartItem::getQuantity).sum();
        return new CartSummaryResponse(cart.getId(), itemCount, cart.getTotalPrice());
    }

    /**
     * Updates total price of the user's cart by applying the given price difference.
     *
     * @param priceDifference amount to be added or subtracted from the current total price
     */
    protected void updateCartTotalPrice(BigDecimal priceDifference) {
        Cart currentUserCart = getCartByAuthenticatedUser();
        currentUserCart.setTotalPrice(currentUserCart.getTotalPrice().add(priceDifference));
        cartRepository.save(currentUserCart);
    }

    /**
     * Clear all items from the user's cart and resets the total price to zero.
     * Orphan removal will handle the deletion of {@code CartItem} objects.
     */
    public void clearCart() {
        Cart currentUserCart = getCartByAuthenticatedUser();
        List<CartItem> cartItems = currentUserCart.getCartItems();

        // orphanRemoval will handle the deletion
        cartItems.clear();

        currentUserCart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(currentUserCart);
    }

    /**
     * Creates a new cart for the authenticated user with initial values.
     *
     * @return newly created {@link Cart}
     */
    private Cart createCart() {
        Cart cart = new Cart(0L, authService.getCurrentUser(), List.of(), BigDecimal.ZERO);
        return cartRepository.save(cart);
    }

}
