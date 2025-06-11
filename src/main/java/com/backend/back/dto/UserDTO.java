package com.backend.back.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private int id;
    private String userId;
    private String nickName;
    private String userPw;
    private int userGrade;
    private String userStringGrade;
    private LocalDateTime createAt;
    private LocalDateTime lastLogin;
}
