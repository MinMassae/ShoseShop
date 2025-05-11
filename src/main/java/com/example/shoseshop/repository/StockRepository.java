package com.example.shoseshop.repository;

import com.example.shoseshop.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findByProductId(Long productId); // 상품 ID로 재고 목록 조회
}