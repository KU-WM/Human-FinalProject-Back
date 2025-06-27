package com.backend.back.controller;

import com.backend.back.dto.FilePath;
import com.backend.back.dto.ImageDTO;
import com.backend.back.mapper.AudioMapper;
import com.backend.back.mapper.ImageMapper;
import com.backend.back.service.DeleteFileService;
import com.backend.back.service.GenerateImageService;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

@RestController
@RequestMapping("/image")
public class ImageController {

    private final String IMAGE_DIR = FilePath.IMAGE_DIR.getMessage();

    @Autowired
    private GenerateImageService generateImageService;

    @Autowired
    private DeleteFileService deleteFileService;

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private AudioMapper audioMapper;

    @GetMapping("/list")
    public List<ImageDTO> getImages(HttpServletRequest request) {
        return imageMapper.getUserImages(Integer.parseInt(request.getAttribute("id").toString()));
    }

    @GetMapping("/listall")
    public List<ImageDTO> getAllImages() {
        return imageMapper.getImages();
    }

    @GetMapping("/templistall")
    public List<ImageDTO> getAllTempImages() {
        return imageMapper.getTempImages();
    }

    @PostMapping("/delete")
    public void deleteImage(@RequestBody HashMap<String, String> message) {
        int id = Integer.parseInt(message.get("id"));
        ImageDTO image = imageMapper.getImage(id);

        deleteFileService.deleteAudioFiles(id);
        imageMapper.deleteImage(id);
        deleteFileService.deleteFile(image.getSavePath());
    }

    @PostMapping("/tempdelete")
    public void deleteTempImage(@RequestBody HashMap<String, String> message) {
        int id = Integer.parseInt(message.get("id"));
        ImageDTO image = imageMapper.getTempImage(id);

        deleteFileService.deleteTempAudioFiles(id);
        imageMapper.deleteTempImageFromId(id);
        deleteFileService.deleteFile(image.getSavePath());
    }

    // 입력: 유저 입력(message) / 출력: audioDTO
    @PostMapping("/generate")
    public Mono<Object> generator(@RequestBody HashMap<String, Object> message, HttpServletRequest request) {
        JSONParser parser = new JSONParser();
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setUserInput(message.get("message").toString());

        return generateImageService.generatePrompt(message.get("message").toString())
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
            .flatMap(prompt -> generateImageService.generateImage(prompt).map(response -> {
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

                if(request.getAttribute("isLogin") == "true") {
                    imageDTO.setCreateBy(Integer.parseInt(request.getAttribute("id").toString()));
                    imageMapper.saveImage(imageDTO);
                }
                else {
                    imageDTO.setTempUserUUID(request.getAttribute("userId").toString());
                    imageMapper.saveTempImage(imageDTO);
                }

                return Map.of(
                        "url", uuidFile,
                        "imageId", imageDTO.getId()
                );
            }));
    }
}
