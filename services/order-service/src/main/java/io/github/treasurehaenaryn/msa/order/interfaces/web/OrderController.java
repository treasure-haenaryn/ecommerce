package io.github.treasurehaenaryn.msa.order.interfaces.web;

import io.github.treasurehaenaryn.msa.order.application.OrderService;
import io.github.treasurehaenaryn.msa.order.domain.Order;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 주문 생성 REST API. Saga의 시작점.
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request.customerId(), request.amount());
        OrderResponse response = new OrderResponse(order.getId(), order.getCustomerId(), order.getAmount(), order.getStatus());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response, "주문이 생성되었습니다."));
    }
}
