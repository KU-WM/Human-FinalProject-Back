package com.backend.back.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AudioDTO {
    private int id;
    private String userInput;
    private String prompt;
    private String savePath;
    private String saveName;
    private LocalDateTime createAt;
    private int createBy;
    private String TempUserUUID;
    private int createFrom;
}
