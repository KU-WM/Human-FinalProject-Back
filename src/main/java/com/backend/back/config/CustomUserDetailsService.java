package com.backend.back.config;

import com.backend.back.dto.UserDTO;
import com.backend.back.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


@Configuration
@EnableWebSecurity
public class CustomUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        UserDTO member = userMapper.findByUserId(userId);
//        System.out.println("\n\n\n" + member + "\n\n\n");

        if(member == null) {
            new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId);
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(member.getUserId())
                .password(member.getUserPw())
                .roles(Integer.toString(member.getUserGrade()))
                .build();
    }
}