package com.example.shoseshop.controller;

import com.example.shoseshop.domain.Member;
import com.example.shoseshop.entity.*;
import com.example.shoseshop.jwtoken.JwtToken;
import com.example.shoseshop.dto.*;
import com.example.shoseshop.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class UserCartApiController {

    private final JwtToken jwtToken;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final StockRepository stockRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    // 장바구니 목록 조회
    @GetMapping("/list")
    public ResponseEntity<?> getCartList(HttpServletRequest request) {
        String token = jwtToken.resolveToken(request);

        if (token == null || !jwtToken.validateToken(token)) {
            return ResponseEntity.status(403).body("ログインが必要です。");
        }

        String userId = jwtToken.getUserIdFromToken(token);
        List<Cart> cartList = cartRepository.findByUserUserId(userId);

        List<CartResponseDTO> responseList = cartList.stream().map(cart -> {
            CartResponseDTO dto = new CartResponseDTO();
            dto.setId(cart.getId());
            dto.setProductName(cart.getProduct().getName());
            dto.setPrice(cart.getProduct().getPrice());
            dto.setQuantity(cart.getQuantity());
            dto.setSize(Integer.parseInt(cart.getSize()));

            List<SizeStockDTO> availableSizes = stockRepository.findByProductId(cart.getProduct().getId())
                    .stream()
                    .filter(stock -> stock.getQuantity() > 0)
                    .map(stock -> {
                        SizeStockDTO sizeStockDTO = new SizeStockDTO();
                        sizeStockDTO.setSize(Integer.parseInt(stock.getSize()));
                        sizeStockDTO.setStock(stock.getQuantity());
                        return sizeStockDTO;
                    })
                    .toList();

            dto.setAvailableSizes(availableSizes);

            return dto;
        }).toList();

        return ResponseEntity.ok(responseList);
    }

    // 장바구니 수정
    @PutMapping("/update")
    public ResponseEntity<?> updateCart(@RequestBody CartUpdateRequestDTO dto, HttpServletRequest request) {
        String token = jwtToken.resolveToken(request);

        if (token == null || !jwtToken.validateToken(token)) {
            return ResponseEntity.status(403).body("ログインが必要です。");
        }

        String userId = jwtToken.getUserIdFromToken(token);

        Cart cart = cartRepository.findById(dto.getCartId())
                .orElseThrow(() -> new IllegalArgumentException("カートが見つかりません。"));

        if (!cart.getUser().getUserId().equals(userId)) {
            return ResponseEntity.status(403).body("自分のカートだけ修正可能です。");
        }

        cart.setSize(dto.getSize().toString());
        cart.setQuantity(dto.getQuantity());

        cartRepository.save(cart);

        return ResponseEntity.ok("カート情報を修正しました！");
    }

    // 장바구니 삭제
    @DeleteMapping("/delete/{cartId}")
    public ResponseEntity<?> deleteCart(@PathVariable Long cartId, HttpServletRequest request) {
        String token = jwtToken.resolveToken(request);

        if (token == null || !token.isBlank() && !jwtToken.validateToken(token)) {
            return ResponseEntity.status(403).body("ログインが必要です。");
        }

        String userId = jwtToken.getUserIdFromToken(token);

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("カートが見つかりません。"));

        if (!cart.getUser().getUserId().equals(userId)) {
            return ResponseEntity.status(403).body("自分のカートだけ削除可能です。");
        }

        cartRepository.delete(cart);
        return ResponseEntity.ok("カートから削除しました！");
    }

    // 주문 생성
    @PostMapping("/order")
    public ResponseEntity<?> createOrder(@RequestBody List<Long> cartIdList, HttpServletRequest request) {
        String token = jwtToken.resolveToken(request);

        if (token == null || !jwtToken.validateToken(token)) {
            return ResponseEntity.status(403).body("ログインが必要です。");
        }

        String userId = jwtToken.getUserIdFromToken(token);
        Member user = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("ユーザー情報が見つかりません。"));

        List<Cart> cartList = cartRepository.findAllById(cartIdList);

        if (cartList.isEmpty()) {
            return ResponseEntity.badRequest().body("選択されたカートが存在しません。");
        }

        // 주문 생성
        Order order = new Order();
        order.setUser(user);
        order.setStatus("注文待機");
        orderRepository.save(order);

        // 주문상품 생성
        for (Cart cart : cartList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cart.getProduct());
            orderItem.setSize(cart.getSize());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setPrice(cart.getProduct().getPrice());

            orderItemRepository.save(orderItem);
        }

        // 장바구니 삭제
        cartRepository.deleteAll(cartList);

        return ResponseEntity.ok("注文が作成されました！");
    }
}
