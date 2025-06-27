package com.backend.back.config;

import java.io.IOException;
import java.time.Duration;

import com.backend.back.mapper.UserMapper;
import com.backend.back.service.Temp2UserService;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 로그인 성공시 처리 되는 클래스
@Configuration
@EnableWebSecurity
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private Temp2UserService temp2UserService;

    private final JwtUtil jwtUtil;

    public LoginSuccessHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        // 기본 URL 지정
        setDefaultTargetUrl("/mypage");
    }

    // 로그인 성공시 실행
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // UserDetails 에서 인증한 정보 가져옴
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userId = userDetails.getUsername();

        // 마지막 로그인 시간 update
        userMapper.lastLogin(userId);

        // 로그인 한 유저의 고유 id(DB의 pk) 가져옴
        int id = userMapper.findByUserId(userId).getId();

        // userDetails 에서 사용자 권한 가져옴 - 그냥 위에 userDTO로 받고 뽑아도 될듯?
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        // 1. JWT / RefreshToken 생성
        String accessToken = jwtUtil.generateToken(userDetails.getUsername(), role, id);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

        // 2. RefreshToken -> 쿠키에 저장
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None") // 필요시 "Lax" 또는 "None"
                .maxAge(Duration.ofDays(7))
                .build();

        // response에 쿠키를 전송
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 로그인 하기 전에 생성된 데이터를 유저와 매칭
        temp2UserService.tempToUser(id, request);

        Cookie[] cookies = request.getCookies();
        String uuid = null;

        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("userId".equals(c.getName())) {
                    uuid = c.getValue();
                    break;
                }
            }
        }

        if (userMapper.findUuid(uuid) == null) {
            userMapper.uuidSetting(uuid);
        }
        userMapper.uuidMatching(id, uuid);

        // 3. AccessToken → JSON 응답
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"accessToken\": \"" + accessToken + "\"," +
                " \"userGrade\": \"" + role + "\"}");
    }
}