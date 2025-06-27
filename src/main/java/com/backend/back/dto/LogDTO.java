package com.backend.back.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogDTO {
    private int id;
    private String clientIp;
    private LocalDateTime accessTime;
    private String requestMethod;
    private String requestLocation;
    private int statusCode;
    private int bytesSent;
    private String uuid;
}
