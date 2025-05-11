package com.example.shoseshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String brand;
    private Integer price;
    private Integer stock;         // 재고 수량
    private Integer soldCount;     // 판매된 수량

    private String imageUrl;       // 이미지 경로 (예: /images/item1.jpg)

    private LocalDateTime createdAt; // 등록일

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.soldCount == null) this.soldCount = 0;
    }
}