package com.example.shoseshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class OrderPageController {

    @GetMapping("/order/confirm")
    public String orderConfirmPage(@RequestParam(name = "cartIds") List<Integer> cartIds, Model model) {
        model.addAttribute("cartIds", cartIds); // orderConfirm.html 에 넘겨줄 데이터

        return "order_confirm";
    }
}