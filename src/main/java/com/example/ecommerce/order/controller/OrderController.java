package com.example.ecommerce.order.controller;

import com.example.ecommerce.order.payload.request.CancelOrderRequest;
import com.example.ecommerce.order.payload.request.PlaceOrderRequest;
import com.example.ecommerce.order.payload.response.OrderResponse;
import com.example.ecommerce.order.service.OrderService;
import com.example.ecommerce.shared.payload.PaginatedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * Retrieves all orders with pagination.
     *
     * @param pageable pagination information
     * @return a {@link ResponseEntity} containing a {@link PaginatedResponse} of {@link OrderResponse}
     */
    @GetMapping
    public ResponseEntity<PaginatedResponse<OrderResponse>> getAllOrders(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllOrdersForCurrentUser(pageable));
    }

    /**
     * Retrieves an order by its unique identifier.
     *
     * @param orderId the unique identifier of the order to be retrieved
     * @return a {@link ResponseEntity} containing the {@link OrderResponse}
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    /**
     * Places a new order.
     *
     * @param placeOrderRequest the {@link PlaceOrderRequest} containing the details of the order to be placed
     * @return a {@link ResponseEntity} containing the created {@link OrderResponse}
     */
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody PlaceOrderRequest placeOrderRequest) {
        return ResponseEntity.ok(orderService.placeOrder(placeOrderRequest));
    }

    /**
     * Cancels an existing order.
     *
     * @param request the {@link CancelOrderRequest} containing the order ID and optional cancellation reason
     * @return a {@link ResponseEntity} indicating the cancellation was successful
     */
    @DeleteMapping("/cancel")
    public ResponseEntity<Void> cancelOrder(@RequestBody CancelOrderRequest request) {
        orderService.cancelOrder(request);
        return ResponseEntity.ok().build();
    }

}
