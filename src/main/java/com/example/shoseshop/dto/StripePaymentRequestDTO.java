package com.example.shoseshop.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StripePaymentRequestDTO {
    private String orderId;
    private Integer amount;
    private List<Long> cartIds;
    private Long productId;
    private boolean direct;
    private Integer quantity;
    private String size;
}
