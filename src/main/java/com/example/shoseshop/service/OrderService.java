package com.example.shoseshop.service;

import com.example.shoseshop.domain.Member;
import com.example.shoseshop.entity.Order;
import com.example.shoseshop.entity.OrderItem;
import com.example.shoseshop.repository.OrderItemRepository;
import com.example.shoseshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public Map<Order, List<OrderItem>> getUserOrders(Member user) {
        List<Order> orders = orderRepository.findByUser_UserIdOrderByDateDesc(user.getUserId());
        return orders.stream().collect(Collectors.toMap(
                o -> o,
                o -> orderItemRepository.findByOrder(o)
        ));
    }
}