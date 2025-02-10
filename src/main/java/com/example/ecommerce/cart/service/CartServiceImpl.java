package com.example.ecommerce.cart.service;

import com.example.ecommerce.auth.service.UserContextService;
import com.example.ecommerce.cart.mapper.CartMapper;
import com.example.ecommerce.cart.model.Cart;
import com.example.ecommerce.cart.model.CartItem;
import com.example.ecommerce.cart.payload.response.CartResponse;
import com.example.ecommerce.cart.payload.response.CartSummaryResponse;
import com.example.ecommerce.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final UserContextService userContextService;

    private final CartMapper cartMapper;
    private final CartRepository cartRepository;

    @Override
    public Cart getCartByAuthenticatedUser() {
        return cartRepository
                .findByUser(userContextService.getCurrentUsername())
                .orElseGet(this::createCart);
    }

    @Override
    public CartResponse getCart() {
        return cartMapper.mapToResponse(getCartByAuthenticatedUser());
    }

    @Override
    public CartSummaryResponse getCartSummary() {
        Cart cart = getCartByAuthenticatedUser();
        int itemCount = cart.getCartItems().stream().mapToInt(CartItem::getQuantity).sum();
        return new CartSummaryResponse(cart.getId(), itemCount, cart.getTotalPrice());
    }

    @Override
    public void updateCartTotalPrice(BigDecimal priceDifference) {
        Cart currentUserCart = getCartByAuthenticatedUser();
        currentUserCart.setTotalPrice(currentUserCart.getTotalPrice().add(priceDifference));
        cartRepository.save(currentUserCart);
    }

    @Override
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
