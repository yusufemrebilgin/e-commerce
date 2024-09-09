package com.example.ecommerce.service;

import com.example.ecommerce.exception.address.AddressNotFoundException;
import com.example.ecommerce.exception.cart.EmptyCartException;
import com.example.ecommerce.mapper.OrderMapper;
import com.example.ecommerce.model.Address;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.model.User;
import com.example.ecommerce.model.enums.OrderStatus;
import com.example.ecommerce.payload.dto.OrderDto;
import com.example.ecommerce.payload.request.order.CreateOrderRequest;
import com.example.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;

    private final CartService cartService;
    private final PaymentService paymentService;

    @Transactional
    public OrderDto placeOrder(CreateOrderRequest request) {

        Cart cart = cartService.getCartByAuthenticatedUser();

        if (cart.isEmpty()) {
            throw new EmptyCartException();
        }

        User user = cart.getUser();
        List<Address> addresses = user.getAddresses();
        if (addresses == null || addresses.isEmpty()) {
            throw new AddressNotFoundException("Address must be defined to place an order");
        }

        Address deliveryAddress = addresses.stream()
                .filter(a -> Objects.equals(a.getId(), request.addressId()))
                .findFirst()
                .orElseThrow(() -> new AddressNotFoundException(request.addressId()));

        Order order = Order.builder()
                .user(cart.getUser())
                .address(deliveryAddress)
                .orderStatus(OrderStatus.PENDING)
                .totalPrice(cart.getTotalPrice())
                .build();

        List<CartItem> cartItems = cart.getCartItems();
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .isDiscountApplied(cartItem.isDiscountApplied())
                    .totalPrice(cartItem.getTotalPrice())
                    .totalDiscountAmount(cartItem.getTotalDiscountAmount())
                    .build();

            orderItems.add(item);
        }

        order.setOrderItems(orderItems);
        order = orderRepository.save(order);

        // processPayment method always return true for now (to simulate payment process)
        boolean paymentSuccess = paymentService.processPayment(order, order.getTotalPrice(), request.paymentMethod());

        if (paymentSuccess) {
            order.setOrderStatus(OrderStatus.SUCCESS);
            cartService.emptyCartAndUpdateStocks();
        } else {
            order.setOrderStatus(OrderStatus.FAILED);
        }

        return orderMapper.mapToDto(orderRepository.save(order));
    }

}
