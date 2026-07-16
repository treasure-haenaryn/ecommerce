package io.github.treasurehaenaryn.msa.order.interfaces.web;

import io.github.treasurehaenaryn.msa.order.domain.OrderStatus;

import java.math.BigDecimal;

/**
 * 주문 응답 DTO.
 */
public record OrderResponse(
        String orderId,
        String customerId,
        String productId,
        int quantity,
        BigDecimal amount,
        OrderStatus status
) {
}
