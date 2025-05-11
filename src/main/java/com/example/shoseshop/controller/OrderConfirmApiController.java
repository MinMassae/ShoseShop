package com.example.shoseshop.controller;

import com.example.shoseshop.domain.Member;
import com.example.shoseshop.dto.OrderItemResponseDTO;
import com.example.shoseshop.dto.OrderResponseDTO;
import com.example.shoseshop.entity.Cart;
import com.example.shoseshop.jwtoken.JwtToken;
import com.example.shoseshop.repository.CartRepository;
import com.example.shoseshop.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderConfirmApiController {

    private final JwtToken jwtToken;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;

    // 선택된 상품 주문 확인 API
    @GetMapping("/confirm")
    public ResponseEntity<?> orderConfirm(
            @RequestParam("cartIds") List<Long> cartIds,
            HttpServletRequest request
    ) {
        // 1. 토큰 확인
        String token = jwtToken.resolveToken(request);

        if (token == null || !jwtToken.validateToken(token)) {
            return ResponseEntity.status(403).body("ログインが必要です。");
        }

        // 2. 사용자 인증
        String userId = jwtToken.getUserIdFromToken(token);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません。"));

        // 3. 선택된 cartIds로 카트 목록 조회
        List<Cart> carts = cartRepository.findAllById(cartIds);

        // 4. 조회한 카트 목록을 OrderItemResponseDTO 리스트로 변환
        List<OrderItemResponseDTO> itemList = carts.stream()
                .map(cart -> {
                    OrderItemResponseDTO item = new OrderItemResponseDTO();
                    item.setProductName(cart.getProduct().getName());
                    item.setSize(cart.getSize());
                    item.setQuantity(cart.getQuantity());
                    item.setPrice(cart.getProduct().getPrice());
                    return item;
                })
                .collect(Collectors.toList());

        // 5. 최종 응답 객체 생성
        OrderResponseDTO response = new OrderResponseDTO();
        response.setName(member.getName());
        response.setPhone(member.getPhone());
        response.setAddress(member.getAddress());
        response.setDate("");
        response.setStatus("注文準備中");
        response.setItems(itemList);

        return ResponseEntity.ok(response);
    }
}
