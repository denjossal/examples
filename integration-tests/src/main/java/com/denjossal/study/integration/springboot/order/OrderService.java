package com.denjossal.study.integration.springboot.order;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrderService(OrderRepository orderRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public OrderEntity placeOrder(String customerId, String productId, int quantity, double total) {
        var order = new OrderEntity(customerId, productId, quantity, total);
        order = orderRepository.save(order);

        kafkaTemplate.send("order-events", order.getId(),
                """
                {"event":"OrderPlaced","orderId":"%s","customerId":"%s","productId":"%s","quantity":%d,"total":%.2f}
                """.formatted(order.getId(), customerId, productId, quantity, total));

        return order;
    }

    @Transactional
    public OrderEntity confirmOrder(String orderId) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        order.setStatus(OrderEntity.OrderStatus.CONFIRMED);
        order = orderRepository.save(order);

        kafkaTemplate.send("order-events", orderId,
                """
                {"event":"OrderConfirmed","orderId":"%s"}
                """.formatted(orderId));

        return order;
    }

    @Transactional
    public OrderEntity cancelOrder(String orderId) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        order.setStatus(OrderEntity.OrderStatus.CANCELLED);
        order = orderRepository.save(order);

        kafkaTemplate.send("order-events", orderId,
                """
                {"event":"OrderCancelled","orderId":"%s"}
                """.formatted(orderId));

        return order;
    }

    public List<OrderEntity> getOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public List<OrderEntity> getOrdersByStatus(OrderEntity.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
}
