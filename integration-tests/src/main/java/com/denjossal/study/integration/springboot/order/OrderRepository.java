package com.denjossal.study.integration.springboot.order;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, String> {
    List<OrderEntity> findByCustomerId(String customerId);

    List<OrderEntity> findByStatus(OrderEntity.OrderStatus status);
}
