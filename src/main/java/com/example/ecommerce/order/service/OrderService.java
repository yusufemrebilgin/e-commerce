package com.example.ecommerce.order.service;

import com.example.ecommerce.address.exception.AddressNotFoundException;
import com.example.ecommerce.cart.exception.EmptyCartException;
import com.example.ecommerce.order.exception.OrderNotFoundException;
import com.example.ecommerce.order.payload.request.CancelOrderRequest;
import com.example.ecommerce.order.payload.request.PlaceOrderRequest;
import com.example.ecommerce.order.payload.response.OrderResponse;
import com.example.ecommerce.payment.exception.PaymentFailedException;
import com.example.ecommerce.shared.payload.PaginatedResponse;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing orders in the e-commerce system.
 * Provides methods to place, retrieve, and cancel orders.
 */
public interface OrderService {

    /**
     * Retrieves order details as a {@link OrderResponse} object.
     *
     * @param orderId ID of the order to retrieve
     * @return {@link OrderResponse} containing order details
     * @throws OrderNotFoundException if no order is found with the given ID
     */
    OrderResponse getOrderById(String orderId);

    /**
     * Retrieves all orders with pagination support for authenticated user.
     *
     * @param pageable pagination information
     * @return a paginated list of {@link OrderResponse}
     */
    PaginatedResponse<OrderResponse> getAllOrdersForCurrentUser(Pageable pageable);

    /**
     * Places an order for the authenticated user by processing items in their cart.
     *
     * @param request the {@link PlaceOrderRequest} containing order details
     * @return newly created {@link OrderResponse}
     * @throws EmptyCartException       if the user's cart is empty
     * @throws AddressNotFoundException if the provided address ID is invalid
     * @throws PaymentFailedException   if payment processing fails
     */
    OrderResponse placeOrder(PlaceOrderRequest request);

    /**
     * Cancels an order if it has been not canceled yet and restores product stock.
     *
     * @param request the {@link CancelOrderRequest} containing the order ID to be canceled
     * @throws OrderNotFoundException if no order is found with the given ID
     * @throws IllegalStateException  if the order is already canceled
     */
    void cancelOrder(CancelOrderRequest request);

}
