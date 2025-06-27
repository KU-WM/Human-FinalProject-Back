package com.backend.back.controller;

import com.backend.back.config.CustomUserDetailsService;
import com.backend.back.config.JwtUtil;
import com.backend.back.dto.UserDTO;
import com.backend.back.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/check")
    public boolean checkId(@RequestBody String userId) {

        return userMapper.findByUserId(userId) == null;
    }

    @GetMapping("/isAdmin")
    public ResponseEntity<UserDTO> userInfo(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            try {
                // JWT 토큰이 유효성 검증을 통과 하면 claim(토큰내 정보)을 반환
                Claims claim = jwtUtil.validateToken(jwt);

                // 유저의 로그인 ID를 가져옴 (Subject는 JWT의 claim의 일종임)
                String username = claim.getSubject();

//                System.out.println("Username from JWT: " + username);

                // JWT로 인증된 유저의 정보를 UserDetails 객체로 불러옴
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                // 실제 관리자 인지 확인
//                System.out.println(userDetails.getAuthorities().toString().equals("[ROLE_10]"));
                if(userDetails.getAuthorities().toString().equals("[ROLE_10]")) {
                    // 로그인 상태와 로그인 유저의 id(DB의 pk인 id임)를 request에 저장

                    UserDTO userInfo = userMapper.findByUserId(username);
                    return ResponseEntity.ok().body(userInfo);
                }
                else {
                    // Role_10 즉 관리자가 아니면 403 금지됨 에러
                    return ResponseEntity.status(403).body(null);
                }
            }
            catch (Exception e) {
                // JWT 인증 실패등 오류가 발생 하면 401에러 발생 시킴
                return ResponseEntity.status(401).body(null);
            }
        }
        else {
            return ResponseEntity.status(401).body(null);
        }
    }

    @GetMapping("/cookieInit")
    public String cookieInit(HttpServletRequest req, HttpServletResponse res) {
        Cookie[] cookies = req.getCookies();
        String userId = null;

        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("userId".equals(c.getName())) {
                    userId = c.getValue();
                    break;
                }
            }
        }

        // 쿠키 없으면 생성
        if (userId == null) {
            userId = UUID.randomUUID().toString();
            Cookie newCookie = new Cookie("userId", userId);
            newCookie.setMaxAge(60 * 60 * 24 * 365); // 1년
            newCookie.setPath("/");
            res.addCookie(newCookie);
            userMapper.uuidSetting(userId);
        }

        return "Your ID: " + userId;
    }


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody HashMap<String, Object> userInfo) {
        UserDTO user = new UserDTO();

        String  encodedPassword = passwordEncoder.encode(userInfo.get("userPw").toString());

        user.setUserId(userInfo.get("userId").toString());
        user.setNickName(userInfo.get("nickName").toString());
        user.setUserPw(encodedPassword);

        userMapper.register(user);

        return ResponseEntity.ok().body("회원 가입 성공!");
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        ResponseCookie expiredCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(0) // 즉시 만료9
                .build();

        String userUuid = UUID.randomUUID().toString();
        ResponseCookie replaceUuidCookie = ResponseCookie.from("userId", userUuid)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(Duration.ofDays(365)) // 즉시 만료9
                .build();

        userMapper.uuidSetting(userUuid);

        response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, replaceUuidCookie.toString());
        return ResponseEntity.ok().build();
    }
}
