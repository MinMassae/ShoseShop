package com.example.shoseshop.config;

import com.example.shoseshop.jwtoken.JwtAuthenticationFilter;
import com.example.shoseshop.jwtoken.JwtToken;
import com.example.shoseshop.service.MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtToken jwtToken;

    public SecurityConfig(JwtToken jwtToken) {
        this.jwtToken = jwtToken;
    }

    // ✅ JWT 인증 필터 Bean 등록
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(MemberService memberService) {
        return new JwtAuthenticationFilter(jwtToken, memberService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthenticationFilter jwtFilter) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/login", "/register", "/api/login", "/home", "/api/home",
                                "/favicon.ico",
                                "/css/**", "/js/**", "/images/**",
                                "/items", "/items/**",
                                "/mypage", "/mypage/**",
                                "/cart/**", "/order/**",
                                "/api/payment/success", "/api/payment/fail", "/api/payment/cancel",
                                "/mypage/orders", "/board/**"

                        ).permitAll()
                        .requestMatchers(
                                "/api/mypage/**",
                                "/api/cart/**",
                                "/api/order/**",
                                "/api/payment/**",
                                "/api/stripe/**",
                                "/api/orders",
                                "/api/board_list/**"
                        ).authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
