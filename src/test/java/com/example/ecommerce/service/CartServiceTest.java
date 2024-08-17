package com.example.ecommerce.service;

import com.example.ecommerce.exception.CartItemNotFoundException;
import com.example.ecommerce.mapper.CartItemMapper;
import com.example.ecommerce.mapper.CartMapper;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.payload.dto.CartDto;
import com.example.ecommerce.payload.dto.CartItemDto;
import com.example.ecommerce.payload.request.cart.CreateCartItemRequest;
import com.example.ecommerce.payload.request.cart.UpdateCartItemRequest;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.util.AuthUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.BDDAssertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    CartService cartService;

    @Mock
    AuthUtils authUtils;

    @Mock
    ProductService productService;

    @Mock
    CartRepository cartRepository;

    @Mock
    CartItemRepository cartItemRepository;

    @Mock
    CartMapper cartMapper;

    @Mock
    CartItemMapper cartItemMapper;

    @Test
    void givenAuthenticatedUsername_whenCartFound_returnCart() {
        // given
        Cart expected = new Cart();
        given(authUtils.getCurrentUsername()).willReturn("test_user");
        given(cartRepository.findByUser(anyString())).willReturn(Optional.of(expected));

        // when
        Cart actual = cartService.getCartByAuthenticatedUser();

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void givenAuthenticatedUsername_whenCartNotFoundCreateCart_returnCreatedCart() {
        // given
        Cart expected = new Cart();
        given(authUtils.getCurrentUsername()).willReturn("test_user");
        given(cartRepository.findByUser(anyString())).willReturn(Optional.empty());
        given(cartRepository.save(any(Cart.class))).willReturn(expected);

        // when
        Cart actual = cartService.getCartByAuthenticatedUser();

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void givenCartItemId_whenCartItemFound_returnCartItem() {
        // given
        UUID cartItemId = UUID.randomUUID();

        CartItem expected = CartItem.builder()
                .id(cartItemId)
                .build();

        given(cartItemRepository.findById(cartItemId)).willReturn(Optional.of(expected));

        // when
        CartItem actual = cartService.getCartItemById(cartItemId);

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
    }

    @Test
    void givenCartItemId_whenCartItemNotFound_throwCartItemNotFoundException() {
        // given
        UUID cartItemId = UUID.randomUUID();
        given(cartItemRepository.findById(cartItemId)).willReturn(Optional.empty());

        // when
        CartItemNotFoundException ex = catchThrowableOfType(
                () -> cartService.getCartItemById(cartItemId),
                CartItemNotFoundException.class
        );

        // then
        then(ex).isNotNull();
        then(ex).hasMessageContaining(String.valueOf(cartItemId));
    }

    @Test
    void givenAuthenticatedUsername_whenCartFound_returnCartDto() {
        // given
        CartDto expected = CartDto.builder().build();
        given(authUtils.getCurrentUsername()).willReturn("test_user");
        given(cartRepository.findByUser(anyString())).willReturn(Optional.of(new Cart()));
        given(cartMapper.mapToDto(any(Cart.class))).willReturn(expected);

        // when
        CartDto actual = cartService.getCart();

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void givenAuthenticatedUsername_whenCartNotFoundCreateCart_returnCartDto() {
        // given
        CartDto expected = CartDto.builder().build();
        given(authUtils.getCurrentUsername()).willReturn("test_user");
        given(cartRepository.findByUser(anyString())).willReturn(Optional.empty());
        given(cartRepository.save(any(Cart.class))).willReturn(new Cart());
        given(cartMapper.mapToDto(any(Cart.class))).willReturn(expected);

        // when
        CartDto actual = cartService.getCart();

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void givenCreateCartItemRequestWithValidDiscount_whenCartItemExistsUpdateExistingItem_returnCartItemDto() {
        // given
        Cart cart = Cart.builder()
                .id(1L)
                .totalPrice(BigDecimal.valueOf(800))
                .build();

        Product product = Product.builder()
                .id(UUID.randomUUID())
                .isDiscountAvailable(true)
                .discountPercentage(20d)
                .price(BigDecimal.valueOf(200))
                .discountedPrice(BigDecimal.valueOf(160)) // 200 - (200 * 0.2)
                .build();

        CartItem existingCartItem = CartItem.builder()
                .quantity(5)
                .isDiscountApplied(true)
                .totalPrice(BigDecimal.valueOf(800))
                .totalDiscountAmount(BigDecimal.valueOf(200))
                .build();

        CreateCartItemRequest request = new CreateCartItemRequest(
                String.valueOf(product.getId()),
                5
        );

        CartItemDto expected = CartItemDto.builder()
                .quantity(10)
                .totalPrice(BigDecimal.valueOf(1600))
                .discount(BigDecimal.valueOf(400))
                .build();

        given(authUtils.getCurrentUsername()).willReturn("test_user");
        given(cartRepository.findByUser(anyString())).willReturn(Optional.of(cart));
        given(productService.getProductById(product.getId())).willReturn(product);
        given(cartItemRepository.existsByCartIdAndProductId(cart.getId(), product.getId())).willReturn(true);
        given(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())).willReturn(existingCartItem);
        given(cartItemRepository.save(existingCartItem)).willReturn(existingCartItem);
        given(cartItemMapper.mapToDto(existingCartItem)).willReturn(expected);

        // when
        CartItemDto actual = cartService.addItemToCart(request);

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        then(existingCartItem.getQuantity()).isEqualTo(expected.quantity());
        then(existingCartItem.getTotalPrice()).isEqualByComparingTo(expected.totalPrice());
        then(existingCartItem.getTotalDiscountAmount()).isEqualByComparingTo(expected.discount());
    }

    @Test
    void givenCreateCartItemRequestWithoutDiscount_whenCartItemNotExistsCreateCartItem_returnCartItemDto() {
        // given
        Cart cart = Cart.builder()
                .id(1L)
                .totalPrice(BigDecimal.valueOf(1000))
                .build();

        Product product = Product.builder()
                .id(UUID.randomUUID())
                .price(BigDecimal.valueOf(200))
                .build();

        CartItem createdCartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(5)
                .unitPrice(BigDecimal.valueOf(200))
                .totalPrice(BigDecimal.valueOf(1000))
                .build();

        CreateCartItemRequest request = new CreateCartItemRequest(
                String.valueOf(product.getId()),
                5
        );

        CartItemDto expected = CartItemDto.builder()
                .quantity(5)
                .totalPrice(BigDecimal.valueOf(1000))
                .build();

        given(authUtils.getCurrentUsername()).willReturn("test_user");
        given(cartRepository.findByUser(anyString())).willReturn(Optional.of(cart));
        given(productService.getProductById(product.getId())).willReturn(product);
        given(cartItemRepository.existsByCartIdAndProductId(cart.getId(), product.getId())).willReturn(false);
        given(cartItemRepository.save(any(CartItem.class))).willReturn(createdCartItem);
        given(cartItemMapper.mapToDto(any(CartItem.class))).willReturn(expected);

        // when
        CartItemDto actual = cartService.addItemToCart(request);

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        then(createdCartItem.getQuantity()).isEqualTo(expected.quantity());
        then(createdCartItem.getTotalPrice()).isEqualByComparingTo(expected.totalPrice());
    }

    @Test
    void givenUpdateCartItemRequestAndCartItemId_whenCartItemUpdated_returnUpdatedCartItemDto() {
        // given
        Cart cart = Cart.builder()
                .totalPrice(BigDecimal.valueOf(1000))
                .build();

        CartItem existingCartItem = CartItem.builder()
                .quantity(5)
                .unitPrice(BigDecimal.valueOf(40))
                .totalPrice(BigDecimal.valueOf(200))
                .build();

        UpdateCartItemRequest request = new UpdateCartItemRequest(8);

        CartItemDto expected = CartItemDto.builder()
                .quantity(8)
                .totalPrice(BigDecimal.valueOf(320)) // 40 * 8
                .build();

        given(authUtils.getCurrentUsername()).willReturn("test_user");
        given(cartRepository.findByUser(anyString())).willReturn(Optional.of(cart));
        given(cartItemRepository.findById(existingCartItem.getId())).willReturn(Optional.of(existingCartItem));
        given(cartItemRepository.save(existingCartItem)).willReturn(existingCartItem);
        given(cartItemMapper.mapToDto(existingCartItem)).willReturn(expected);

        // when
        CartItemDto actual = cartService.updateItemInCart(existingCartItem.getId(), request);

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        then(existingCartItem.getQuantity()).isEqualTo(expected.quantity());
        then(existingCartItem.getTotalPrice()).isEqualByComparingTo(expected.totalPrice());
    }

    @Test
    void givenCartItemId_whenCartItemFound_deleteExistingCartItem() {
        // given
        CartItem existingCartItem = CartItem.builder()
                .totalPrice(BigDecimal.valueOf(1700))
                .build();

        Cart cart = Cart.builder()
                .totalPrice(BigDecimal.valueOf(3000))
                .build();

        given(authUtils.getCurrentUsername()).willReturn("test_user");
        given(cartRepository.findByUser(anyString())).willReturn(Optional.of(cart));
        given(cartItemRepository.findById(existingCartItem.getId())).willReturn(Optional.of(existingCartItem));

        // when
        cartService.deleteItemFromCart(existingCartItem.getId());

        // then
        then(cart.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(1300));

        verify(cartItemRepository, times(1)).delete(any(CartItem.class));
    }

}
