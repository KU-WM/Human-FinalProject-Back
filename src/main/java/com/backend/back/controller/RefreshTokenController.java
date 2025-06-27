package com.backend.back.controller;

import com.backend.back.config.CustomUserDetailsService;
import com.backend.back.config.JwtUtil;
import com.backend.back.dto.UserDTO;
import com.backend.back.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;

@RestController
@RequestMapping("/token")
public class RefreshTokenController {

    @Autowired
    private UserMapper userMapper;

    private final JwtUtil jwtUtil;

    public RefreshTokenController(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
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
        String id = claim.getSubject();
        System.out.println("4");
        // 3. 유저 정보 조회 및 권한 가져오기
        UserDTO user = userMapper.findByUserId(id);

        System.out.println("5");
        // 4. 새로운 Access Token 생성
        String newAccessToken = jwtUtil.generateToken(user.getUserId(), Integer.toString(user.getUserGrade()), user.getId());
        System.out.println("6");
        // 5. 응답에 새 Access Token 전달
        return ResponseEntity.ok(Collections.singletonMap("accessToken", newAccessToken));
    }
}
