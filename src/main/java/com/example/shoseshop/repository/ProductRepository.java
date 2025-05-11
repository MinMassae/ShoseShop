package com.example.shoseshop.repository;

import com.example.shoseshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findTop3ByOrderByOrderCountDesc(); // BEST 상품
    List<Product> findTop3ByOrderByCreatedAtDesc();  // NEW 상품
    List<Product> findAllByOrderByIdDesc();          // 전체 목록
    Page<Product> findByBrendName(String name, Pageable pageable);
}