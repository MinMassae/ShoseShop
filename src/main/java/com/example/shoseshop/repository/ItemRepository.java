package com.example.shoseshop.repository;

import com.example.shoseshop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // 최신 등록 순 (NEW)
    List<Item> findTop6ByOrderByCreatedAtDesc();

    // 판매량 많은 순 (BEST)
    List<Item> findTop6ByOrderBySoldCountDesc();
}
