package com.backend.back.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// BackEnd로의 직접 접근 방지 / 기능별 권한 설정
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtFilter;
    private final CustomUserDetailsService customUserDetailsService;
    private final LoginSuccessHandler loginSuccessHandler;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, LoginSuccessHandler loginSuccessHandler,
                          JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
        this.customUserDetailsService = customUserDetailsService;
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http
                .csrf(CsrfConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // ★ 세션 완전 비활성화 - JWT 토큰과 충돌
                )
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/.well-known/**").permitAll()
                        .requestMatchers("/image/list", "/audio/list").authenticated()
                        .requestMatchers("/admin/**").hasRole("10")
                        .anyRequest().permitAll()
                )
                .formLogin((auth) ->
                        auth
                                .loginProcessingUrl("/login") // 로그인 요청 url
                                .successHandler(loginSuccessHandler)    // 로그인 성공시 처리 로직
                                .failureUrl("/login?loginerror=1")  // 로그인 실패시 로직 / react 에서 막아둠
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // 모든 동작 처리 전 JWT 토큰 검증
                .exceptionHandling((exceptions) -> exceptions   // 예외 발생시 401 에러 리턴 -> react에서 refreshToken 사용
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"error\": \"Unauthorized\"}");
                        })
                );

        return http.build();
    }

    // UserDetails의 정보와 사용자 입력 정보를 비교
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
        return builder.build();
    }

    // 비밀번호 암호화
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // well-known으로 에러 나는거 방지
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/.well-known/**");
    }
}