package com.backend.back.controller;

import com.backend.back.dto.ImageDTO;
import com.backend.back.mapper.UserMapper;
import com.backend.back.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserMapper userMapper;

    @Autowired
    ImageService imageService;


    @GetMapping("/login")
    public void login() {

    }

    @ResponseBody
    @PostMapping("/answer")
    public Object answer(@RequestBody HashMap<String, Object> message) {
        userMapper.testmessage(message.get("message").toString());
        return message;
    }

    @ResponseBody
    @PostMapping("/generate")
    public ImageDTO generate(@RequestBody HashMap<String, Object> message) {
        String input = message.get("message").toString();

        return imageService.generateImage(input);
    }
}
