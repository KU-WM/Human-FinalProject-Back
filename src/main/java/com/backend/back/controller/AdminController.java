package com.backend.back.controller;

import com.backend.back.dto.ImageDTO;
import com.backend.back.mapper.AudioMapper;
import com.backend.back.mapper.ImageMapper;
import com.backend.back.mapper.UserMapper;
import com.backend.back.service.DeleteFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private AudioMapper audioMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DeleteFileService deleteFileService;

    @PostMapping("/chgrade")
    public Map<String, Object> changeUserGrade(@RequestBody HashMap<String, String> message) {
        int grade = Integer.parseInt(message.getOrDefault("grade", "0"));
        int id = Integer.parseInt(message.getOrDefault("userId", "0"));

        if(grade != 0 && id != 0) {
            try {
                userMapper.updateUserGrade(grade, id);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return Map.of(
                        "message", "Failed"
                );
            }
        }

        return Map.of(
                "message", "Change Success"
        );
    }

    @PostMapping("/deleteuser")
    public Map<String, Object> deleteUser(@RequestBody HashMap<String, String> message) {
        int userId = Integer.parseInt(message.getOrDefault("userId", "0"));

        if(userId == 0) {
            return Map.of(
                    "message", "Wrong Input"
            );
        }

        try {
            List<ImageDTO> images = imageMapper.getUserImages(userId);
            for(ImageDTO image : images) {
                deleteFileService.deleteAudioFiles(image.getId());
                deleteFileService.deleteFile(image.getSavePath());
                imageMapper.deleteImage(image.getId());
            }

            userMapper.deleteUser(userId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Map.of(
                    "message", "Failed"
            );
        }

        return Map.of(
                "message", "Success"
        );
    }
}
