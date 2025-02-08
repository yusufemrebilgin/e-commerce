package com.example.ecommerce.cart.service;

import com.example.ecommerce.auth.service.UserContextService;
import com.example.ecommerce.cart.mapper.CartMapper;
import com.example.ecommerce.cart.model.Cart;
import com.example.ecommerce.cart.model.CartItem;
import com.example.ecommerce.cart.payload.response.CartResponse;
import com.example.ecommerce.cart.payload.response.CartSummaryResponse;
import com.example.ecommerce.cart.repository.CartRepository;
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

    private final UserContextService userContextService;

    private final CartMapper cartMapper;
    private final CartRepository cartRepository;

    /**
     * Retrieves the authenticated user's username.
     * <p>
     * Although this method is already available in {@code UserContextService},
     * it is defined in {@code CartService} to assist other services like
     * {@code OrderService} in accessing the authenticated user's information
     * without needing to directly inject {@code UserContextService}.
     *
     * @return authenticated user's username
     */
    public String getAuthenticatedUsername() {
        return userContextService.getCurrentUsername();
    }

    /**
     * Retrieves cart for the authenticated user. If no cart exists, a new one is created.
     *
     * @return user's cart
     * @throws IllegalStateException     if a user is not authenticated (by UserContextService)
     * @throws UsernameNotFoundException if a user is not found by specified username (by UserContextService)
     */
    public Cart getCartByAuthenticatedUser() {
        return cartRepository
                .findByUser(userContextService.getCurrentUsername())
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
     * Updates the total price of the user's cart by applying the given price difference.
     *
     * @param priceDifference amount to be added or subtracted from the current total price
     */
    public void updateCartTotalPrice(BigDecimal priceDifference) {
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
        Cart cart = new Cart(null, userContextService.getCurrentUser(), List.of(), BigDecimal.ZERO);
        return cartRepository.save(cart);
    }

}
