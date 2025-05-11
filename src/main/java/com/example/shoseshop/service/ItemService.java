package com.example.shoseshop.service;

import com.example.shoseshop.entity.Product;
import com.example.shoseshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ProductRepository productRepository;

    // 🔹 최신 상품 3개
    public List<Product> getNewItems() {
        return productRepository.findTop3ByOrderByCreatedAtDesc();
    }

    // 🔹 주문 수 기준 인기 상품 3개
    public List<Product> getBestItems() {
        return productRepository.findTop3ByOrderByOrderCountDesc();
    }

    // 🔹 전체 상품 (정렬 기준은 ID 내림차순)
    public List<Product> getAllItems() {
        return productRepository.findAllByOrderByIdDesc();
    }

    // 🔹 전체 상품 - 페이징 처리
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    // 🔹 브랜드 별 상품 - 페이징 처리
    public Page<Product> getProductsByBrand(String brandName, Pageable pageable) {
        return productRepository.findByBrendName(brandName, pageable);
    }
}