package com.example.shoseshop.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartResponseDTO {
    private Long id;
    private String productName;
    private Integer price;
    private Integer quantity;
    private Integer size;
    private List<SizeStockDTO> availableSizes;
}