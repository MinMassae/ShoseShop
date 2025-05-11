package com.example.shoseshop.repository;

import com.example.shoseshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser_UserIdOrderByDateDesc(String userId);
}