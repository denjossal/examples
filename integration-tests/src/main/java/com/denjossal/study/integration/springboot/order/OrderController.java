package com.denjossal.study.integration.springboot.order;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    public record PlaceOrderRequest(String customerId, String productId, int quantity, double total) {}

    @PostMapping
    public ResponseEntity<OrderEntity> placeOrder(@RequestBody PlaceOrderRequest request) {
        var order =
                orderService.placeOrder(request.customerId(), request.productId(), request.quantity(), request.total());
        return ResponseEntity.created(URI.create("/api/orders/" + order.getId()))
                .body(order);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<OrderEntity> confirmOrder(@PathVariable String id) {
        return ResponseEntity.ok(orderService.confirmOrder(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderEntity> cancelOrder(@PathVariable String id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    @GetMapping("/customer/{customerId}")
    public List<OrderEntity> getByCustomer(@PathVariable String customerId) {
        return orderService.getOrdersByCustomer(customerId);
    }
}
