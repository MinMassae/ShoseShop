package com.example.shoseshop.repository;

import com.example.shoseshop.entity.OrderItem;
import com.example.shoseshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
}