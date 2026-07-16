package com.example.msakafka.order.interfaces.web;

import com.example.msakafka.order.domain.OrderStatus;

import java.math.BigDecimal;

/**
 * 주문 응답 DTO.
 */
public record OrderResponse(
        String orderId,
        String customerId,
        BigDecimal amount,
        OrderStatus status
) {
}
