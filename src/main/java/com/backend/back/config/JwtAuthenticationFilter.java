package com.backend.back.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

// 유저가 로그인 상태 인지 JWT 토큰 여부로 파악 하는 필터
// 로그인 상태가 아니면 무작위 UUID를 userId 쿠키에 담아 전송 하여 유저 구별
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    // @Autowired와 기능상 동일 / JwtAuthenticationFilter 생성자
    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // request의 Header에서 인증 정보를 불러옴
        String authHeader = request.getHeader("Authorization");

//        System.out.println("Authorization Header: " + authHeader);

        // 인증 정보가 없거나 Bearer 로 시작 하지 않는 경우 = JWT 토큰이 없는 경우
        // else문 에서 UUID를 쿠키로 발급 해서 유저 구분
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);

//            System.out.println("Extracted JWT: " + jwt);
            try {
                // JWT 토큰이 유효성 검증을 통과 하면 claim(토큰내 정보)을 반환
                Claims claim = jwtUtil.validateToken(jwt);

                // 유저의 로그인 ID를 가져옴 (Subject는 JWT의 claim의 일종임)
                String username = claim.getSubject();

//                System.out.println("Username from JWT: " + username);

                // JWT로 인증된 유저의 정보를 UserDetails 객체로 불러옴
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SpringSecurity의 인증에 정보 주입
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // 로그인 상태와 로그인 유저의 id(DB의 pk인 id임)를 request에 저장
                request.setAttribute("isLogin", "true");
                request.setAttribute("id", claim.get("id", Integer.class));
            }
            catch (Exception e) {
                // JWT 인증 실패등 오류가 발생 하면 401에러 발생 시킴
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
                return;
            }
        }
        else {
            // request 에서 쿠키 가져 오기
            Cookie[] cookies = request.getCookies();
            String userId = null;

            // 쿠기 존재시 userId 라는 쿠키 찾아서 추출
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
                // 랜덤 UUID 발급
                userId = UUID.randomUUID().toString();
                Cookie newCookie = new Cookie("userId", userId);
                newCookie.setMaxAge(60 * 60 * 24 * 365); // 1년
                newCookie.setPath("/");
                response.addCookie(newCookie);
            }

            request.setAttribute("isLogin", "false");
            request.setAttribute("userId", userId);
        }

        // 필터 통과후 기존 request/response 유지한 상태로 진행
        filterChain.doFilter(request, response);
    }
}
