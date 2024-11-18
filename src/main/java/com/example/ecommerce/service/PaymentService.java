package com.example.ecommerce.service;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.Payment;
import com.example.ecommerce.model.enums.OrderStatus;
import com.example.ecommerce.model.enums.PaymentMethod;
import com.example.ecommerce.model.enums.PaymentStatus;
import com.example.ecommerce.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    /**
     * Process payment for a given order with a specified amount and payment method.
     * <p>Note: It simulates payment process. Payment is always considered successful
     * for now.
     *
     * @param order         order for which payment is processed
     * @param paymentAmount amount to be paid
     * @param paymentMethod payment method to be used (e.g., DEBIT_CARD, CREDIT_CARD)
     * @return true if payment is processed successfully
     */
    protected boolean processPayment(Order order, BigDecimal paymentAmount, String paymentMethod) {

        order.setOrderStatus(OrderStatus.PROCESSING);

        Payment payment = Payment.builder()
                .order(order)
                .amount(paymentAmount)
                .paymentMethod(PaymentMethod.fromString(paymentMethod))
                .paymentDate(LocalDateTime.now())
                .paymentStatus(PaymentStatus.SUCCESS)
                .transactionId(UUID.randomUUID().toString())
                .build();

        paymentRepository.save(payment);

        // Simulating payment process
        // Payment is always considered successful for now
        return true;
    }

}
