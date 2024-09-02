package com.example.ecommerce.service;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.Payment;
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

    protected boolean processPayment(Order order, BigDecimal paymentAmount, String paymentMethod) {

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
