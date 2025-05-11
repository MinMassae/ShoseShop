package com.example.shoseshop.controller;

import com.example.shoseshop.dto.MemberDTO;
import com.example.shoseshop.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController{

    @Autowired
    private MemberService memberService;

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute MemberDTO dto, Model model) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            model.addAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
            return "register";
        }

        try {
            memberService.register(dto);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }
}
