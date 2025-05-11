package com.example.shoseshop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartUpdateRequestDTO {
    private Long cartId;
    private Integer size;
    private Integer quantity;
}