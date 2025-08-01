package com.example.shoseshop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StripePaymentItemDTO {
    private String productName;
    private String size;
    private Integer quantity;
    private Integer price;
}
