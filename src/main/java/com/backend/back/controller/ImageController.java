package com.backend.back.controller;

import com.backend.back.dto.FilePath;
import com.backend.back.dto.ImageDTO;
import com.backend.back.mapper.ImageMapper;
import com.backend.back.service.ImageService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/image")
public class ImageController {

    private final String IMAGE_DIR = FilePath.IMAGE_DIR.getMessage();

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageMapper imageMapper;


    @ResponseBody
    @GetMapping("/list")
    public List<ImageDTO> getAllImages() {
        return imageMapper.getImages();
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

                        String uuidFile = UUID.randomUUID() + ".png";

                        imageDTO.setSaveName(uuidFile);
                        imageDTO.setSavePath(IMAGE_DIR + uuidFile);

                        try {
                            // Base64 디코딩
                            byte[] imageBytes = Base64.getDecoder().decode(inlineData.get("data").toString());

                            // 파일로 저장
                            try (OutputStream stream = new FileOutputStream(IMAGE_DIR + uuidFile)) {
                                stream.write(imageBytes);
                            }

                            System.out.println("✅ 이미지 저장 완료: " + IMAGE_DIR + uuidFile);
                        } catch (Exception e) {
                            System.err.println("❌ 이미지 저장 실패: " + e.getMessage());
                            e.printStackTrace();
                        }

                        imageMapper.saveImage(imageDTO);

                        return uuidFile;
                    });
                });
    }
}
