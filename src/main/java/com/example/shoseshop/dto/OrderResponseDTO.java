package com.example.shoseshop.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderResponseDTO {
    private String name;
    private String address;
    private String phone;
    private String date;
    private String status;
    private List<OrderItemResponseDTO> items;
}
