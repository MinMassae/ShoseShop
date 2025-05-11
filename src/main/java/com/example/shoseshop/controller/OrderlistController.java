package com.example.shoseshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrderlistController {

    @GetMapping("/mypage/orders")
    public String OrderListPage() {
        return "order_list";
    }
}

