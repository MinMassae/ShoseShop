package com.example.shoseshop.controller;

import com.example.shoseshop.domain.Member;
import com.example.shoseshop.jwtoken.JwtToken;
import com.example.shoseshop.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController  // ✅ 여긴 RestController만 사용
@RequestMapping("/api")
public class LoginController {

    private final MemberService memberService;
    private final JwtToken jwtToken;

    @Autowired
    public LoginController(MemberService memberService, JwtToken jwtToken) {
        this.memberService = memberService;
        this.jwtToken = jwtToken;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String userId,
                                   @RequestParam String password) {

        Member member = memberService.login(userId, password);

        if (member != null) {
            String token = jwtToken.createToken(userId, "user");

            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + token)
                    .body("로그인 성공");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("ユーザーIDまたはパスワードが正しくありません。");
        }
    }
}