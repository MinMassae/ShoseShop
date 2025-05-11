package com.example.shoseshop.controller;


import com.example.shoseshop.dto.MemberDTO;
import com.example.shoseshop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PutMapping("/update")
    public ResponseEntity<?> updateMember(@RequestBody MemberDTO dto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        memberService.updateMember(userId, dto);
        return ResponseEntity.ok("会員情報を更新しました。");
    }
}