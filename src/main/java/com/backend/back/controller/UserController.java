package com.backend.back.controller;

import com.backend.back.dto.ImageDTO;
import com.backend.back.mapper.UserMapper;
import com.backend.back.service.ImageService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
    public Mono<Object> generator(@RequestBody HashMap<String, Object> message) {
        JSONParser parser = new JSONParser();
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setUserInput(message.get("message").toString());

        return imageService.generatePrompt(message.get("message").toString())
                .map(response -> {
                    JSONObject output = null;

                    try {
                        output = (JSONObject) parser.parse(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    JSONArray candidates = (JSONArray) output.get("candidates");
                    JSONObject candidate = (JSONObject) candidates.get(0);
                    JSONObject content = (JSONObject) candidate.get("content");
                    JSONArray parts = (JSONArray) content.get("parts");
                    JSONObject part = (JSONObject) parts.get(0);

                    return part.get("text").toString();
                })
                .flatMap(prompt -> {
                    return imageService.generateImage(prompt).map(response -> {
                        imageDTO.setPrompt(prompt);
                        JSONObject output = null;

                        try {
                            output = (JSONObject) parser.parse(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        JSONArray candidates = (JSONArray) output.get("candidates");
                        JSONObject candidate = (JSONObject) candidates.get(0);
                        JSONObject content = (JSONObject) candidate.get("content");
                        JSONArray parts = (JSONArray) content.get("parts");
                        JSONObject part = (JSONObject) parts.get(1);
                        JSONObject inlineData = (JSONObject) part.get("inlineData");
                        imageDTO.setImage(inlineData.get("data").toString());

                        userMapper.saveImage(imageDTO);

                        return inlineData.get("data");
                    });
                });
    }
}
