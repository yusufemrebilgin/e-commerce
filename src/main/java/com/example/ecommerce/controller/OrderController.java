package com.example.ecommerce.controller;

import com.example.ecommerce.payload.dto.OrderDto;
import com.example.ecommerce.payload.request.order.CreateOrderRequest;
import com.example.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderDto> placeOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.placeOrder(request));
    }

}
