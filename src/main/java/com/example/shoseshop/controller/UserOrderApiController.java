package com.example.shoseshop.controller;

import com.example.shoseshop.dto.OrderItemResponseDTO;
import com.example.shoseshop.dto.OrderResponseDTO;
import com.example.shoseshop.entity.Order;
import com.example.shoseshop.entity.OrderItem;
import com.example.shoseshop.jwtoken.JwtToken;
import com.example.shoseshop.repository.OrderItemRepository;
import com.example.shoseshop.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class UserOrderApiController {

    private final JwtToken jwtToken;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    // 주문 내역 조회
    @GetMapping
    public ResponseEntity<?> getOrderList(HttpServletRequest request) {
        String token = jwtToken.resolveToken(request);

        if (token == null || !jwtToken.validateToken(token)) {
            return ResponseEntity.status(403).body("ログインが必要です。");
        }

        String userId = jwtToken.getUserIdFromToken(token);

        // 1. 사용자 주문 목록 가져오기
        List<Order> orders = orderRepository.findByUser_UserIdOrderByDateDesc(userId);

        // 2. 주문 → DTO 변환
        List<OrderResponseDTO> responseList = orders.stream().map(order -> {
            List<OrderItem> items = orderItemRepository.findByOrder(order);

            List<OrderItemResponseDTO> itemDTOList = items.stream().map(item -> {
                OrderItemResponseDTO itemDTO = new OrderItemResponseDTO();
                itemDTO.setProductName(item.getProduct().getName());
                itemDTO.setSize(item.getSize());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setPrice(item.getPrice());
                return itemDTO;
            }).collect(Collectors.toList());

            OrderResponseDTO orderDTO = new OrderResponseDTO();
            orderDTO.setDate(order.getDate().toString());
            orderDTO.setStatus(order.getStatus());
            orderDTO.setItems(itemDTOList);

            return orderDTO;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }
}
