package com.example.ecommerce.order.service;

import com.example.ecommerce.address.exception.AddressNotFoundException;
import com.example.ecommerce.address.model.Address;
import com.example.ecommerce.auth.model.User;
import com.example.ecommerce.auth.service.UserContextService;
import com.example.ecommerce.cart.exception.EmptyCartException;
import com.example.ecommerce.cart.model.Cart;
import com.example.ecommerce.cart.model.CartItem;
import com.example.ecommerce.cart.service.CartService;
import com.example.ecommerce.order.exception.OrderNotFoundException;
import com.example.ecommerce.order.mapper.OrderMapper;
import com.example.ecommerce.order.model.Order;
import com.example.ecommerce.order.model.enums.OrderStatus;
import com.example.ecommerce.order.payload.request.CancelOrderRequest;
import com.example.ecommerce.order.payload.request.PlaceOrderRequest;
import com.example.ecommerce.order.payload.response.OrderResponse;
import com.example.ecommerce.order.repository.OrderRepository;
import com.example.ecommerce.payment.exception.PaymentFailedException;
import com.example.ecommerce.payment.service.PaymentService;
import com.example.ecommerce.product.service.ProductService;
import com.example.ecommerce.shared.payload.PaginatedResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @InjectMocks
    OrderServiceImpl orderService;

    @Mock
    OrderMapper orderMapper;

    @Mock
    OrderRepository orderRepository;

    @Mock
    CartService cartService;

    @Mock
    PaymentService paymentService;

    @Mock
    ProductService productService;

    @Mock
    UserContextService userContextService;

    @Test
    void givenOrderId_whenOrderFound_thenReturnOrderResponse() {
        // given
        String orderId = "order-id";
        Order order = mock(Order.class);
        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
        given(orderMapper.mapToResponse(order)).willReturn(mock(OrderResponse.class));

        // when
        OrderResponse response = orderService.getOrderById(orderId);

        // then
        then(response).isNotNull();
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderMapper, times(1)).mapToResponse(order);
    }

    @Test
    void givenOrderId_whenOrderNotFound_thenThrowOrderNotFoundException() {
        // given
        String orderId = "order-id";
        given(orderRepository.findById(orderId)).willReturn(Optional.empty());

        // when & then
        OrderNotFoundException ex = catchThrowableOfType(
                OrderNotFoundException.class,
                () -> orderService.getOrderById(orderId)
        );

        then(ex).isNotNull();
        then(ex).hasMessageContaining(orderId);
        verify(orderMapper, never()).mapToResponse(any(Order.class));
    }

    @Test
    void givenCurrentUserAndPageable_whenGetAllOrderForUser_thenReturnPaginatedResponse() {
        // given
        String username = "test-user";
        Pageable pageable = mock(Pageable.class);

        List<Order> orders = List.of(new Order(), new Order());
        Page<Order> orderPage = new PageImpl<>(orders);

        given(userContextService.getCurrentUsername()).willReturn(username);
        given(orderRepository.findAllByUser(username, pageable)).willReturn(orderPage);
        given(orderMapper.mapToPaginatedResponse(orderPage)).willReturn(mock(PaginatedResponse.class));

        // when
        PaginatedResponse<OrderResponse> response = orderService.getAllOrdersForCurrentUser(pageable);

        // then
        then(response).isNotNull();
        verify(orderRepository, times(1)).findAllByUser(username, pageable);
        verify(orderMapper, times(1)).mapToPaginatedResponse(orderPage);
    }

    @Test
    void givenPlaceOrderRequest_whenOrderPlacedSuccessfully_thenReturnOrderResponse() {
        // given
        PlaceOrderRequest request = new PlaceOrderRequest(1L, "DEBIT_CARD");

        Address address = Address.builder()
                .id(request.addressId())
                .build();

        User currentUser = User.builder()
                .username("test-user")
                .addresses(List.of(address))
                .build();

        Cart currentUserCart = mock(Cart.class);
        given(currentUserCart.getUser()).willReturn(currentUser);
        given(cartService.getCartByAuthenticatedUser()).willReturn(currentUserCart);
        given(paymentService.processPayment(any(), any(), eq(request.paymentMethod()))).willReturn(true);
        given(orderRepository.save(any(Order.class))).willReturn(new Order());
        given(orderMapper.mapToResponse(any(Order.class))).willReturn(mock(OrderResponse.class));

        // when
        OrderResponse response = orderService.placeOrder(request);

        // then
        then(response).isNotNull();
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderMapper, times(1)).mapToResponse(any(Order.class));
    }

    @Test
    void givenPlaceOrderRequest_whenUserCartIsEmpty_thenThrowEmptyCartException() {
        // given
        Cart userCart = Cart.builder()
                .cartItems(List.of())
                .build();

        given(cartService.getCartByAuthenticatedUser()).willReturn(userCart);

        // when & then
        EmptyCartException ex = catchThrowableOfType(
                EmptyCartException.class,
                () -> orderService.placeOrder(new PlaceOrderRequest(null, ""))
        );

        then(ex).isNotNull();
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void givenPlaceOrderRequest_whenDeliveryAddressNotFound_thenThrowAddressNotFoundException() {
        // given
        PlaceOrderRequest request = new PlaceOrderRequest(1L, "");

        Cart userCart = mock(Cart.class);
        given(cartService.getCartByAuthenticatedUser()).willReturn(userCart);
        given(userCart.isEmpty()).willReturn(false);
        given(userCart.getUser()).willReturn(new User());

        // when & then
        AddressNotFoundException ex = catchThrowableOfType(
                AddressNotFoundException.class,
                () -> orderService.placeOrder(request)
        );

        then(ex).isNotNull();
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void givenPlaceOrderRequest_whenPaymentProcessingFails_thenSetStatusFailedAndThrowPaymentFailedExceptionAndSave() {
        // given
        PlaceOrderRequest request = new PlaceOrderRequest(1L, "UNKNOWN_METHOD");

        Address address = new Address();
        address.setId(request.addressId());

        User user = User.builder()
                .addresses(List.of(address))
                .build();

        Cart userCart = Cart.builder()
                .user(user)
                .cartItems(List.of(mock(CartItem.class)))
                .build();

        given(cartService.getCartByAuthenticatedUser()).willReturn(userCart);

        ArgumentCaptor<Order> order = ArgumentCaptor.forClass(Order.class);
        given(orderRepository.save(order.capture())).willReturn(new Order());

        // when & then
        PaymentFailedException ex = catchThrowableOfType(
                PaymentFailedException.class,
                () -> orderService.placeOrder(request)
        );

        then(ex).isNotNull();
        then(order.getValue().getOrderStatus()).isEqualTo(OrderStatus.FAILED);
    }

    @Test
    void givenCancelOrderRequest_whenOrderCanceledSuccessfully_thenSetStatusCancelledAndUpdateStocks() {
        // given
        CancelOrderRequest request = new CancelOrderRequest("existing-order-id");

        Order order = new Order();
        given(orderRepository.findById(request.orderId())).willReturn(Optional.of(order));

        // when
        orderService.cancelOrder(request);

        // then
        then(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

}