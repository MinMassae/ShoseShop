package com.example.shoseshop.api;

import com.example.shoseshop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HomeApiController {

    private final ItemService itemService;

    @GetMapping("/home")
    public ResponseEntity<?> getHomeData(Authentication authentication) {
        Map<String, Object> result = new HashMap<>();
        result.put("newItems", itemService.getNewItems());
        result.put("bestItems", itemService.getBestItems());

        // 로그인 사용자만 userId를 추가
        if (authentication != null && authentication.isAuthenticated()) {
            String userId = authentication.getName();
            result.put("userId", userId);
        }

        return ResponseEntity.ok(result);
    }
}