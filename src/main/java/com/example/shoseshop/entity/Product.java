package com.example.shoseshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "brend_id")
    private Brend brend;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    private String imageUrl;

    private String description; // 상품 설명

    @Column(name = "order_count")
    private Integer orderCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
