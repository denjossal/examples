package com.denjossal.study.integration.springboot.order;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double total;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public OrderEntity() {}

    public OrderEntity(String customerId, String productId, int quantity, double total) {
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.total = total;
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getTotal() { return total; }
    public OrderStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    public void setStatus(OrderStatus status) { this.status = status; }

    public enum OrderStatus {
        PENDING, CONFIRMED, SHIPPED, CANCELLED
    }
}
