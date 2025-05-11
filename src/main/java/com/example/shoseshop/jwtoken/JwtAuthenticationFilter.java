package com.example.shoseshop.jwtoken;

import com.example.shoseshop.domain.Member;
import com.example.shoseshop.service.MemberService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtToken jwtToken;
    private final MemberService memberService;

    public JwtAuthenticationFilter(JwtToken jwtToken, MemberService memberService) {
        this.jwtToken = jwtToken;
        this.memberService = memberService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // 정적 리소스나 인증 불필요 경로 제외
        if (uri.startsWith("/css") || uri.startsWith("/js") || uri.startsWith("/images")
                || uri.equals("/") || uri.startsWith("/login") || uri.startsWith("/register")
                || uri.startsWith("/home") || uri.startsWith("/api/home")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);
        System.out.println("📦 필터 진입: " + uri);
        System.out.println("📦 토큰: " + token);

        if (token != null && jwtToken.validateToken(token)) {
            String userId = jwtToken.getUserIdFromToken(token);
            Member member = memberService.findByUserId(userId);

            // 중요: 인증 정보는 최소한 사용자 정보와 권한을 담아야 함
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(member.getUserId(), null, null);

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken)) {
            if (bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7); // ✅ Bearer 제거
            } else {
                return bearerToken; // ✅ 접두사 없으면 그대로 사용
            }
        }
        return null;
    }

}