package com.example.ecommerce.cart.service;

import com.example.ecommerce.auth.service.UserContextService;
import com.example.ecommerce.cart.mapper.CartMapper;
import com.example.ecommerce.cart.model.Cart;
import com.example.ecommerce.cart.model.CartItem;
import com.example.ecommerce.cart.model.embeddable.ProductInfo;
import com.example.ecommerce.cart.payload.response.CartResponse;
import com.example.ecommerce.cart.payload.response.CartSummaryResponse;
import com.example.ecommerce.cart.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @InjectMocks
    CartServiceImpl cartService;

    @Mock
    UserContextService userContextService;

    @Mock
    CartMapper cartMapper;

    @Mock
    CartRepository cartRepository;

    @Test
    void givenCurrentUsername_whenCartFoundWithUsername_thenReturnCart() {
        // given
        String username = "test-user";
        given(userContextService.getCurrentUsername()).willReturn(username);
        given(cartRepository.findByUser(username)).willReturn(Optional.of(new Cart()));

        // when
        Cart cart = cartService.getCartByAuthenticatedUser();

        // then
        then(cart).isNotNull();
        verify(cartRepository, times(1)).findByUser(username);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void givenCurrentUsername_whenCartNotFound_thenCreateAndReturnNewCart() {
        // given
        given(userContextService.getCurrentUsername()).willReturn("test-user");
        given(cartRepository.findByUser(anyString())).willReturn(Optional.empty());
        given(cartRepository.save(any(Cart.class))).willReturn(new Cart());

        // when
        Cart cart = cartService.getCartByAuthenticatedUser();

        // then
        then(cart).isNotNull();
        verify(cartRepository, times(1)).findByUser(anyString());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void givenCurrentUsername_whenCurrentUserGetCart_thenReturnCartResponse() {
        // given
        given(userContextService.getCurrentUsername()).willReturn("test-user");
        given(cartRepository.findByUser(anyString())).willReturn(Optional.of(new Cart()));
        given(cartMapper.mapToResponse(any(Cart.class))).willReturn(mock(CartResponse.class));

        // when
        CartResponse response = cartService.getCart();

        // then
        then(response).isNotNull();
        verify(cartRepository, times(1)).findByUser(anyString());
        verify(cartMapper, times(1)).mapToResponse(any(Cart.class));
    }

    @Test
    void givenCurrentUsername_whenCurrentUserCartFound_thenConvertAndReturnCartSummaryResponse() {
        // given
        Cart cart = Cart.builder()
                .id(1L)
                .build();

        CartItem item = CartItem.builder()
                .id(UUID.randomUUID().toString())
                .cart(cart)
                .productInfo(new ProductInfo(5, BigDecimal.TEN, BigDecimal.valueOf(5 * 10)))
                .build();

        cart.setCartItems(List.of(item));
        cart.setTotalPrice(item.getProductInfo().getTotalPrice());

        CartSummaryResponse expected = new CartSummaryResponse(
                cart.getId(),
                item.getQuantity(),
                item.getProductInfo().getTotalPrice()
        );

        given(userContextService.getCurrentUsername()).willReturn("test-user");
        given(cartRepository.findByUser(anyString())).willReturn(Optional.of(cart));

        // when
        CartSummaryResponse actual = cartService.getCartSummary();

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        then(actual.itemCount()).isEqualTo(item.getQuantity());
    }

    @Test
    void givenCurrentUserCartAndPriceDifference_whenTotalPriceUpdated_thenSaveUserCart() {
        // given
        Cart cart = Cart.builder()
                .totalPrice(BigDecimal.valueOf(100_000))
                .build();

        given(userContextService.getCurrentUsername()).willReturn("test-user");
        given(cartRepository.findByUser(anyString())).willReturn(Optional.of(cart));

        // when
        cartService.updateCartTotalPrice(BigDecimal.valueOf(50_000));

        // then
        then(cart.getTotalPrice()).isEqualTo(BigDecimal.valueOf(150_000));
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void givenCurrentUserCart_whenCartCleared_thenRemoveItemsAndSetTotalPriceToZero() {
        // given
        Cart cart = Cart.builder()
                .id(1L)
                .build();

        CartItem item1 = CartItem.builder()
                .id(UUID.randomUUID().toString())
                .cart(cart)
                .productInfo(new ProductInfo(5, BigDecimal.TEN, BigDecimal.valueOf(5 * 10)))
                .build();

        CartItem item2 = CartItem.builder()
                .id(UUID.randomUUID().toString())
                .cart(cart)
                .productInfo(new ProductInfo(7, BigDecimal.valueOf(5), BigDecimal.valueOf(7 * 5)))
                .build();

        BigDecimal totalPrice = item1.getProductInfo().getTotalPrice().add(item2.getProductInfo().getTotalPrice());
        cart.setTotalPrice(totalPrice);
        cart.getCartItems().add(item1);
        cart.getCartItems().add(item2);

        given(userContextService.getCurrentUsername()).willReturn("test-user");
        given(cartRepository.findByUser(anyString())).willReturn(Optional.of(cart));

        // when
        cartService.clearCart();

        // then
        then(cart.getCartItems()).hasSize(0);
        then(cart.getTotalPrice()).isEqualTo(BigDecimal.ZERO);
        verify(cartRepository, times(1)).save(cart);
    }

}