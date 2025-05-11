package com.example.shoseshop.controller;

import com.example.shoseshop.domain.Member;
import com.example.shoseshop.entity.Cart;
import com.example.shoseshop.entity.Product;
import com.example.shoseshop.repository.CartRepository;
import com.example.shoseshop.repository.MemberRepository;
import com.example.shoseshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    // 장바구니에 상품 추가
    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestBody Map<String, Object> payload,
                                            Authentication authentication) {

        System.out.println("🧪 인증 객체: " + authentication);

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("ログインが必要です。");
        }

        String userId = authentication.getName(); // JwtToken에서 setSubject(userId)
        System.out.println("🧪 사용자 ID: " + userId);

        Member user = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("該当するユーザーが見つかりませんでした。"));

        Long productId = Long.valueOf(payload.get("productId").toString());
        String size = payload.get("size").toString();
        Integer quantity = Integer.valueOf(payload.get("quantity").toString());

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("ご指定の商品は見つかりませんでした。"));

        Cart existing = cartRepository.findByUserAndProductAndSize(user, product, size).orElse(null);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            cartRepository.save(existing);
        } else {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setProduct(product);
            newCart.setSize(size);
            newCart.setQuantity(quantity);
            cartRepository.save(newCart);
        }

        return ResponseEntity.ok("カートに追加しました。");
    }
}