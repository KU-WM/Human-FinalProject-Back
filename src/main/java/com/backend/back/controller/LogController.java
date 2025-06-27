package com.backend.back.controller;

import com.backend.back.dto.AccessLogDTO;
import com.backend.back.service.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/log")
public class LogController {

    @Autowired
    private LoggingService loggingService;

    @GetMapping("/refresh")
    public List<AccessLogDTO> logTest() throws IOException {
        return loggingService.GetLog();
    }
}
