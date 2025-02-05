package com.example.ecommerce.order.controller;

import com.example.ecommerce.order.payload.request.CreateOrderRequest;
import com.example.ecommerce.order.payload.response.OrderResponse;
import com.example.ecommerce.shared.payload.PaginatedResponse;
import com.example.ecommerce.order.service.OrderService;
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
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    /**
     * Retrieves an order by its unique identifier.
     *
     * @param orderId the unique identifier of the order to be retrieved
     * @return a {@link ResponseEntity} containing the {@link OrderResponse}
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    /**
     * Places a new order.
     *
     * @param createOrderRequest the {@link CreateOrderRequest} containing the details of the order to be placed
     * @return a {@link ResponseEntity} containing the created {@link OrderResponse}
     */
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody CreateOrderRequest createOrderRequest) {
        return ResponseEntity.ok(orderService.placeOrder(createOrderRequest));
    }

    /**
     * Cancels an existing order.
     *
     * @param orderId the unique identifier of the order to be cancelled
     * @return a {@link ResponseEntity} indicating the cancellation was successful
     */
    @DeleteMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }

}
