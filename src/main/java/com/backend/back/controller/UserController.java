package com.backend.back.controller;

import com.backend.back.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserMapper userMapper;


    @GetMapping("/login")
    public String login() {


        return "login";
    }

    @ResponseBody
    @PostMapping("/answer")
    public Object answer(@RequestParam("message") String message) {
        userMapper.testmessage(message);
        return message;
    }
}
