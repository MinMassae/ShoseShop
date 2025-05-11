package com.example.shoseshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartPageController {

    @GetMapping("/mypage/cart")
    public String cartPage() {
        return "cart_list";  // resources/templates/cart_list.html
    }
}
