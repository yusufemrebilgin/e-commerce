package com.example.ecommerce.cart.service;

import com.example.ecommerce.cart.exception.CartItemNotFoundException;
import com.example.ecommerce.cart.mapper.CartItemMapper;
import com.example.ecommerce.cart.model.Cart;
import com.example.ecommerce.cart.model.CartItem;
import com.example.ecommerce.cart.model.embeddable.DiscountInfo;
import com.example.ecommerce.cart.model.embeddable.ProductInfo;
import com.example.ecommerce.cart.payload.request.CreateCartItemRequest;
import com.example.ecommerce.cart.payload.request.UpdateCartItemRequest;
import com.example.ecommerce.cart.payload.response.CartItemResponse;
import com.example.ecommerce.cart.repository.CartItemRepository;
import com.example.ecommerce.product.exception.InsufficientStockException;
import com.example.ecommerce.product.model.Product;
import com.example.ecommerce.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemServiceImplTest {

    @InjectMocks
    CartItemServiceImpl cartItemService;

    @Mock
    CartService cartService;

    @Mock
    ProductService productService;

    @Mock
    CartItemMapper cartItemMapper;

    @Mock
    CartItemRepository cartItemRepository;

    @Test
    void givenCreateCartItemRequest_whenCartItemNotExists_thenCreateNewCartItem() {
        // given
        Cart cart = Cart.builder()
                .id(1L)
                .build();

        Product product = Product.builder()
                .id("product-id")
                .stock(50)
                .price(BigDecimal.TEN)
                .build();

        CreateCartItemRequest request = new CreateCartItemRequest(product.getId(), 1);

        given(cartService.getCartByAuthenticatedUser()).willReturn(cart);
        given(productService.findProductEntityById(anyString())).willReturn(product);
        given(cartItemRepository.findByCartIdAndProductId(anyLong(), anyString())).willReturn(Optional.empty());
        given(cartItemRepository.save(any(CartItem.class))).willReturn(new CartItem());
        given(cartItemMapper.mapToResponse(any(CartItem.class))).willReturn(mock(CartItemResponse.class));

        // when
        CartItemResponse response = cartItemService.addItemToCart(request);

        // then
        then(response).isNotNull();
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        verify(cartItemMapper, times(1)).mapToResponse(any(CartItem.class));
    }

    @Test
    void givenCreateCartItemRequest_whenCartItemFound_thenUpdateExistingCartItem() {
        // given
        Cart cart = Cart.builder()
                .id(1L)
                .build();

        Product product = Product.builder()
                .id("product-id")
                .stock(50)
                .price(BigDecimal.TEN)
                .build();

        CreateCartItemRequest request = new CreateCartItemRequest(product.getId(), 5);

        CartItem cartItem = CartItem.builder()
                .id("cart-item-id")
                .cart(cart)
                .product(product)
                .build();

        given(cartService.getCartByAuthenticatedUser()).willReturn(cart);
        given(productService.findProductEntityById(anyString())).willReturn(product);
        given(cartItemRepository.findByCartIdAndProductId(anyLong(), anyString())).willReturn(Optional.of(cartItem));
        given(cartItemRepository.save(any(CartItem.class))).willReturn(cartItem);
        given(cartItemMapper.mapToResponse(any(CartItem.class))).willReturn(mock(CartItemResponse.class));

        // when
        CartItemResponse response = cartItemService.addItemToCart(request);

        // then
        then(response).isNotNull();
        verify(cartItemRepository, times(1)).save(cartItem);
        verify(cartItemMapper, times(1)).mapToResponse(cartItem);
    }

    @Test
    void givenCreateCartItemRequest_whenRequestedQuantityIsInvalid_thenThrowInsufficientStockException() {
        // given
        Cart cart = Cart.builder()
                .id(1L)
                .build();

        Product product = Product.builder()
                .id("product-id")
                .stock(50)
                .price(BigDecimal.TEN)
                .build();

        CreateCartItemRequest request = new CreateCartItemRequest(product.getId(), 100);

        given(cartService.getCartByAuthenticatedUser()).willReturn(cart);
        given(productService.findProductEntityById(anyString())).willReturn(product);
        given(cartItemRepository.findByCartIdAndProductId(anyLong(), anyString())).willReturn(Optional.empty());
        doThrow(InsufficientStockException.class).when(productService).checkStock(anyString(), eq(request.quantity()));

        // when
        InsufficientStockException ex = catchThrowableOfType(
                InsufficientStockException.class,
                () -> cartItemService.addItemToCart(request)
        );

        // then
        then(ex).isNotNull();
        verify(cartItemRepository, never()).save(any(CartItem.class));
        verify(cartItemMapper, never()).mapToResponse(any(CartItem.class));
    }

    @Test
    void givenUpdateCartItemRequest_whenCartItemFound_thenUpdateAndReturnCartItemResponse() {
        // given
        Cart cart = Cart.builder()
                .id(1L)
                .build();

        Product product = Product.builder()
                .id("product-id")
                .price(BigDecimal.valueOf(50))
                .stock(100)
                .build();

        CartItem cartItem = CartItem.builder()
                .id("item-id")
                .cart(cart)
                .product(product)
                .productInfo(new ProductInfo(5, BigDecimal.TEN, BigDecimal.valueOf(5 * 10)))
                .discountInfo(new DiscountInfo())
                .build();

        UpdateCartItemRequest request = new UpdateCartItemRequest(10);

        given(cartItemRepository.findById(anyString())).willReturn(Optional.of(cartItem));
        given(cartItemRepository.save(any(CartItem.class))).willReturn(cartItem);
        given(cartItemMapper.mapToResponse(any(CartItem.class))).willReturn(mock(CartItemResponse.class));

        // when
        CartItemResponse response = cartItemService.updateItemQuantityInCart(cartItem.getId(), request);

        // then
        then(response).isNotNull();
        then(cartItem.getQuantity()).isEqualTo(10);
        verify(cartItemRepository, times(1)).save(cartItem);
        verify(cartItemMapper, times(1)).mapToResponse(cartItem);
    }

    @Test
    void givenUpdateCartItemRequest_whenCartItemNotFound_thenThrowCartItemNotFoundException() {
        // given
        UpdateCartItemRequest request = new UpdateCartItemRequest(50);
        given(cartItemRepository.findById(anyString())).willReturn(Optional.empty());

        // when
        CartItemNotFoundException thrown = catchThrowableOfType(
                CartItemNotFoundException.class,
                () -> cartItemService.updateItemQuantityInCart("invalid-id", request)
        );

        // then
        then(thrown).isNotNull();
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void givenCartItemId_whenCartItemExists_thenRemoveCartItem() {
        // given
        Cart cart = Cart.builder()
                .id(1L)
                .totalPrice(BigDecimal.valueOf(1000))
                .build();


        Product product = Product.builder()
                .id("product-id")
                .price(BigDecimal.valueOf(50))
                .stock(100)
                .build();

        CartItem cartItem = CartItem.builder()
                .id("item-id")
                .cart(cart)
                .product(product)
                .productInfo(new ProductInfo())
                .discountInfo(new DiscountInfo())
                .build();

        given(cartService.getCartByAuthenticatedUser()).willReturn(cart);
        given(cartItemRepository.findById(anyString())).willReturn(Optional.of(cartItem));

        // when
        cartItemService.removeItemFromCart(cartItem.getId());

        // then
        verify(cartItemRepository, times(1)).delete(cartItem);
    }

}