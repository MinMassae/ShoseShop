package com.example.shoseshop.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaymentRequestDTO {
    private String orderId;
    private Integer amount;
    private List<Long> cartIds;
}