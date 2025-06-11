package com.backend.back.controller;

import com.backend.back.config.CustomUserDetailsService;
import com.backend.back.config.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/token")
public class RefreshTokenController {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public RefreshTokenController(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        System.out.println("Refresh Start");

        // 1. 쿠키에서 refreshToken 꺼내기
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        System.out.println("1");
        String refreshToken = Arrays.stream(cookies)
                .filter(c -> c.getName().equals("refreshToken"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
        System.out.println("2");
        if (refreshToken == null || jwtUtil.validateToken(refreshToken).isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        System.out.println("3");
        // 2. refreshToken에서 username 추출
        Claims claim = jwtUtil.validateToken(refreshToken);
        String username = claim.getSubject();
        System.out.println("4");
        // 3. 유저 정보 조회 및 권한 가져오기
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        System.out.println("5");
        // 4. 새로운 Access Token 생성
        String newAccessToken = jwtUtil.generateToken(username, roles);
        System.out.println("6");
        // 5. 응답에 새 Access Token 전달
        return ResponseEntity.ok(Collections.singletonMap("accessToken", newAccessToken));
    }
}
