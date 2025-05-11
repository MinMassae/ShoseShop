package com.example.shoseshop.repository;

import com.example.shoseshop.entity.Cart;
import com.example.shoseshop.domain.Member;
import com.example.shoseshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    // 사용자의 전체 장바구니 목록
    List<Cart> findByUserUserId(String userId);

    // 사용자의 장바구니에 특정 상품/사이즈가 이미 있는지 확인
    Optional<Cart> findByUserAndProductAndSize(Member user, Product product, String size);

    // 삭제할 때 편하도록
    void deleteByUser(Member user);
}
