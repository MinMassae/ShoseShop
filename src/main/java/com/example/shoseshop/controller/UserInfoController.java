package com.example.shoseshop.controller;

import com.example.shoseshop.domain.Member;
import com.example.shoseshop.dto.MemberDTO;
import com.example.shoseshop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class UserInfoController {

    private final MemberService memberService;

    // SecurityContext에서 현재 로그인된 사용자 정보 가져오기
    @GetMapping
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("로그인이 필요합니다.");
        }

        String userId = authentication.getName(); // JwtAuthenticationFilter에서 set된 값
        Member member = memberService.findByUserId(userId);

        MemberDTO dto = new MemberDTO();
        dto.setUserId(member.getUserId());
        dto.setName(member.getName());
        dto.setAddress(member.getAddress());
        dto.setEmail(member.getEmail());
        dto.setPhone(member.getPhone());

        return ResponseEntity.ok(dto);
    }
}