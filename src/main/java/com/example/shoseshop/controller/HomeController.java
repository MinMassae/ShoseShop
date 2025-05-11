package com.example.shoseshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping({"/","/home"})
    public String homePage() {
        return "home"; // 데이터는 JS(fetch)로 받아서 화면에 표시
    }
}
