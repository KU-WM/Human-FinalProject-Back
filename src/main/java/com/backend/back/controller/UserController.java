package com.backend.back.controller;

import com.backend.back.dto.UserDTO;
import com.backend.back.mapper.UserMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/check")
    public boolean checkId(@RequestBody String userId) {

        return userMapper.findByUserId(userId) == null;
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
                .maxAge(0) // 즉시 만료
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
        return ResponseEntity.ok().build();
    }
}
