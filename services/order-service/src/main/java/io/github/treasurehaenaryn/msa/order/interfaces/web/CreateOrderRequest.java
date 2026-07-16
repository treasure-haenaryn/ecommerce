package io.github.treasurehaenaryn.msa.order.interfaces.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * 주문 생성 요청 DTO.
 */
public record CreateOrderRequest(
        @NotBlank String customerId,
        @NotBlank String productId,
        @Positive int quantity,
        @NotNull @Positive BigDecimal amount
) {
}
