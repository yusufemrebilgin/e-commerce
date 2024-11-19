package com.example.ecommerce.service;

import com.example.ecommerce.exception.address.AddressNotFoundException;
import com.example.ecommerce.exception.cart.EmptyCartException;
import com.example.ecommerce.exception.order.OrderNotFoundException;
import com.example.ecommerce.exception.payment.PaymentFailedException;
import com.example.ecommerce.mapper.OrderMapper;
import com.example.ecommerce.mapper.PaginationMapper;
import com.example.ecommerce.model.Address;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.model.User;
import com.example.ecommerce.model.enums.OrderStatus;
import com.example.ecommerce.payload.request.order.CreateOrderRequest;
import com.example.ecommerce.payload.response.OrderResponse;
import com.example.ecommerce.payload.response.PaginatedResponse;
import com.example.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;

    private final CartService cartService;
    private final PaymentService paymentService;
    private final ProductService productService;

    private final PaginationMapper paginationMapper;

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    /**
     * Retrieves an order by its ID.
     *
     * @param orderId ID of the order to retrieve
     * @return found {@link Order}
     * @throws OrderNotFoundException if order is not found
     */
    protected Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found with id {}", orderId);
                    return new OrderNotFoundException(orderId);
                });
    }

    /**
     * Retrieves order details as a {@link OrderResponse} object.
     *
     * @param orderId ID of the order to retrieve
     * @return {@link OrderResponse} containing order details
     */
    public OrderResponse getOrderById(Long orderId) {
        return orderMapper.mapToResponse(findOrderById(orderId));
    }

    /**
     * Retrieves all orders with pagination support for authenticated user.
     *
     * @param pageable pagination information
     * @return a paginated list of {@link OrderResponse}
     */
    public PaginatedResponse<OrderResponse> getAllOrders(Pageable pageable) {
        String username = cartService.getAuthenticatedUsername();
        return paginationMapper.toPaginatedResponse(orderRepository.findAllByUser(username, pageable), orderMapper);
    }

    /**
     * Places an order for the authenticated user by processing items in their cart.
     *
     * @param createOrderRequest the {@link CreateOrderRequest} containing order details
     * @return newly created {@link OrderResponse}
     * @throws EmptyCartException       if user's cart is empty
     * @throws AddressNotFoundException if specified address is not found
     * @throws PaymentFailedException   if payment fails
     */
    @Transactional
    public OrderResponse placeOrder(CreateOrderRequest createOrderRequest) {

        Cart currentUserCart = cartService.getCartByAuthenticatedUser();
        if (currentUserCart.isEmpty()) {
            throw new EmptyCartException();
        }

        User currentUser = currentUserCart.getUser();
        Address deliveryAddress = getDeliveryAddress(currentUser, createOrderRequest.addressId());
        Order order = createNewOrder(currentUserCart, deliveryAddress);

        try {
            processOrderPayment(order, createOrderRequest.paymentMethod());
            updateProductStocks(order.getOrderItems(), productService::decreaseStock);
        } catch (PaymentFailedException ex) {
            order.setOrderStatus(OrderStatus.FAILED);
            log.error("Payment failed for order {}", order.getId());
            throw ex; // Re-throw to be handled by global exception handler
        } finally {
            orderRepository.save(order);
        }

        log.info("Order placed successfully for user {}", currentUser.getUsername());
        return orderMapper.mapToResponse(order);
    }

    /**
     * Cancels an order if it has been not cancelled yet and restores product stock.
     *
     * @param orderId ID of the order to cancel
     * @throws IllegalStateException if order is already cancelled
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        Order placedOrder = findOrderById(orderId);
        if (placedOrder.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }
        placedOrder.setOrderStatus(OrderStatus.CANCELLED);
        // Resetting product stocks
        updateProductStocks(placedOrder.getOrderItems(), productService::increaseStock);
        orderRepository.save(placedOrder);
    }

    /**
     * Creates a new {@code Order} based on the current user's cart and delivery address.
     *
     * @param currentUserCart cart of the authenticated user
     * @param deliveryAddress delivery address for order
     * @return newly created {@link Order}
     */
    private Order createNewOrder(Cart currentUserCart, Address deliveryAddress) {
        Order newOrder = Order.builder()
                .user(currentUserCart.getUser())
                .address(deliveryAddress)
                .orderStatus(OrderStatus.PENDING)
                .totalPrice(currentUserCart.getTotalPrice())
                .build();

        newOrder.setOrderItems(currentUserCart.getCartItems().stream()
                .map(cartItem -> createNewOrderItemFromCartItem(newOrder, cartItem))
                .toList());

        return newOrder;
    }

    /**
     * Creates a new {@code OrderItem} from a {@code CartItem}.
     *
     * @param order    the {@link Order} to associate item with
     * @param cartItem the {@link CartItem} to convert into an {@link OrderItem}
     * @return newly created {@link OrderItem}
     */
    private OrderItem createNewOrderItemFromCartItem(Order order, CartItem cartItem) {
        return OrderItem.builder()
                .order(order)
                .product(cartItem.getProduct())
                .productInfo(cartItem.getProductInfo())
                .discountInfo(cartItem.getDiscountInfo())
                .build();
    }

    /**
     * Retrieves delivery address for the order based on user's saved addresses.
     *
     * @param user      current authenticated user
     * @param addressId ID of the address to retrieve
     * @return the {@link Address} to be used for the order
     * @throws AddressNotFoundException if address is not found
     */
    private Address getDeliveryAddress(User user, Long addressId) {
        List<Address> userAddresses = user.getAddresses();
        if (userAddresses == null || userAddresses.isEmpty()) {
            throw new AddressNotFoundException("Address must be defined to place an order");
        }

        return userAddresses.stream()
                .filter(address -> Objects.equals(addressId, address.getId()))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Address with id {} not found for user: {}", addressId, user.getUsername());
                    return new AddressNotFoundException(addressId);
                });
    }

    /**
     * Processes order payment using the specified {@link com.example.ecommerce.model.enums.PaymentMethod}.
     *
     * @param order         the {@link Order} to process payment for
     * @param paymentMethod the method to use for the payment
     * @throws PaymentFailedException if payment fails
     */
    private void processOrderPayment(Order order, String paymentMethod) {
        boolean paymentSuccessful = paymentService.processPayment(order, order.getTotalPrice(), paymentMethod);
        if (!paymentSuccessful) {
            throw new PaymentFailedException();
        }
        order.setOrderStatus(OrderStatus.COMPLETED);
    }

    /**
     * Updates product stock based on order items.
     *
     * @param orderItems     list of {@link OrderItem}s in the order
     * @param stockOperation stock operation to perform by {@link ProductService} (increase or decrease)
     */
    private void updateProductStocks(List<OrderItem> orderItems, BiConsumer<UUID, Integer> stockOperation) {
        orderItems.forEach(orderItem -> {
            UUID productId = orderItem.getProduct().getId();
            stockOperation.accept(productId, orderItem.getProductInfo().getQuantity());
        });
    }

}
