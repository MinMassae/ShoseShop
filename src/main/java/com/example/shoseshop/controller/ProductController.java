package com.example.shoseshop.controller;

import com.example.shoseshop.entity.Product;
import com.example.shoseshop.repository.ProductRepository;
import com.example.shoseshop.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    // 전체 상품 or 브랜드별 상품 리스트
    @GetMapping("/items")
    public String listItems(@RequestParam(required = false) String brand,
                            @RequestParam(defaultValue = "0") int page,
                            Model model) {

        Page<Product> productPage;

        if (brand == null || brand.isBlank()) {
            productPage = productRepository.findAll(PageRequest.of(page, 9));
        } else {
            productPage = productRepository.findByBrendName(brand, PageRequest.of(page, 9));
        }

        model.addAttribute("productPage", productPage);
        model.addAttribute("brand", brand);
        return "item_list";
    }

    // 상품 상세 페이지
    @GetMapping("/items/{id}")
    public String itemDetail(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + id));

        model.addAttribute("product", product);
        model.addAttribute("stocks", stockRepository.findByProductId(id)); // 사이즈 및 재고
        return "item_detail";
    }
}