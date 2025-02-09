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
import com.example.ecommerce.order.model.OrderItem;
import com.example.ecommerce.order.model.enums.OrderStatus;
import com.example.ecommerce.order.payload.request.CancelOrderRequest;
import com.example.ecommerce.order.payload.request.PlaceOrderRequest;
import com.example.ecommerce.order.payload.response.OrderResponse;
import com.example.ecommerce.order.repository.OrderRepository;
import com.example.ecommerce.payment.exception.PaymentFailedException;
import com.example.ecommerce.payment.model.enums.PaymentMethod;
import com.example.ecommerce.payment.service.PaymentService;
import com.example.ecommerce.product.service.ProductService;
import com.example.ecommerce.shared.payload.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;

    private final CartService cartService;
    private final PaymentService paymentService;
    private final ProductService productService;
    private final UserContextService userContextService;

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Override
    public OrderResponse getOrderById(String orderId) {
        return orderMapper.mapToResponse(findOrderEntityById(orderId));
    }

    @Override
    public PaginatedResponse<OrderResponse> getAllOrdersForCurrentUser(Pageable pageable) {
        String username = userContextService.getCurrentUsername();
        return orderMapper.mapToPaginatedResponse(orderRepository.findAllByUser(username, pageable));
    }

    @Override
    public OrderResponse placeOrder(PlaceOrderRequest request) {

        Cart currentUserCart = cartService.getCartByAuthenticatedUser();
        if (currentUserCart.isEmpty()) {
            throw new EmptyCartException();
        }

        User currentUser = currentUserCart.getUser();
        Address deliveryAddress = getDeliveryAddress(currentUser, request.addressId());
        Order order = createNewOrder(currentUserCart, deliveryAddress);

        try {
            processOrderPayment(order, request.paymentMethod());
            updateProductStocks(order.getOrderItems(), productService::decreaseStock);
            cartService.clearCart();
        } catch (PaymentFailedException ex) {
            order.setOrderStatus(OrderStatus.FAILED);
            logger.error("Payment failed for order {}", order.getId());
            throw ex; // Re-throw to be handled by global exception handler
        } finally {
            orderRepository.save(order);
        }

        logger.info("Order placed successfully for user '{}'", currentUser.getUsername());
        return orderMapper.mapToResponse(order);
    }

    @Override
    public void cancelOrder(CancelOrderRequest request) {
        Order placedOrder = findOrderEntityById(request.orderId());
        if (placedOrder.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }

        placedOrder.setOrderStatus(OrderStatus.CANCELLED);
        // Resetting product stocks
        updateProductStocks(placedOrder.getOrderItems(), productService::increaseStock);
        orderRepository.save(placedOrder);
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param orderId ID of the order to retrieve
     * @return found {@link Order}
     * @throws OrderNotFoundException if order is not found
     */
    private Order findOrderEntityById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.error("Order not found with id {}", orderId);
                    return new OrderNotFoundException(orderId);
                });
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
                .collect(Collectors.toList()));

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
                    logger.error("Address with id {} not found for user: {}", addressId, user.getUsername());
                    return new AddressNotFoundException(addressId);
                });
    }

    /**
     * Processes order payment using the specified {@link PaymentMethod}.
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
    private void updateProductStocks(List<OrderItem> orderItems, BiConsumer<String, Integer> stockOperation) {
        orderItems.forEach(orderItem -> {
            String productId = orderItem.getProduct().getId();
            stockOperation.accept(productId, orderItem.getProductInfo().getQuantity());
        });
    }

}
