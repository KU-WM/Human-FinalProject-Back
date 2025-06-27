package com.backend.back.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccessLogDTO {
    private int id;
    private String clientIp;
    private LocalDateTime accessTime;
    private String requestMethod;
    private String requestLocation;
    private int statusCode;
    private int responseBytes;
    private String uuid;
}
