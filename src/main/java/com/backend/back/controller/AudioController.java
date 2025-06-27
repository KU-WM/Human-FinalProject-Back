package com.backend.back.controller;

import com.backend.back.dto.AudioDTO;
import com.backend.back.mapper.AudioMapper;
import com.backend.back.service.DeleteFileService;
import com.backend.back.service.GenerateAudioService;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/audio")
public class AudioController {

    @Autowired
    private GenerateAudioService generateAudioService;

    @Autowired
    private DeleteFileService deleteFileService;

    @Autowired
    private AudioMapper audioMapper;

    @GetMapping("/list/{createFrom}")
    public List<AudioDTO> getAudios(@PathVariable String createFrom) {
        return audioMapper.getImageAudios(Integer.parseInt(createFrom));
    }

    @GetMapping("/templist/{createFrom}")
    public List<AudioDTO> getTempAudios(@PathVariable String createFrom) {
        return audioMapper.getTempImageAudios(Integer.parseInt(createFrom));
    }

    @GetMapping("/listall")
    public List<AudioDTO> getAllAudios() {
        return audioMapper.getAudios();
    }

    @PostMapping("/delete")
    public void deleteAudio(@RequestBody HashMap<String, String> message) {
        int id = Integer.parseInt(message.get("id"));
        AudioDTO audio = audioMapper.getAudio(id);

        deleteFileService.deleteFile(audio.getSavePath());

        audioMapper.deleteAudio(id);
    }

    @PostMapping("/tempdelete")
    public void deleteTempAudio(@RequestBody HashMap<String, String> message) {
        int id = Integer.parseInt(message.get("id"));
        AudioDTO audio = audioMapper.getTempAudio(id);

        deleteFileService.deleteFile(audio.getSavePath());

        audioMapper.deleteTempAudioFromId(id);
    }

    // 입력: 유저 입력(message) + 해당 이미지 pk(imageId) / 출력: audioDTO
    @PostMapping("/generate")
    public Object test(@RequestBody HashMap<String, Object> message, HttpServletRequest request) {
        JSONParser parser = new JSONParser();
        AudioDTO audioDTO = new AudioDTO();

        return generateAudioService.generateAudioPrompt(message.get("message").toString())
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
                return generateAudioService.generateAudio(prompt).map(response -> {
                    audioDTO.setUserInput(message.get("message").toString());
                    audioDTO.setPrompt(prompt);

                    JSONObject output = null;

                    try {
                        output = (JSONObject) parser.parse(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    audioDTO.setSaveName(output.get("file_name").toString());
                    audioDTO.setSavePath(output.get("save_path").toString());

                    int sourceImageId = Integer.parseInt(message.get("imageId").toString());
                    audioDTO.setCreateFrom(sourceImageId);

                    if(request.getAttribute("isLogin") == "true" && message.getOrDefault("isOwner", "True").equals("True")) {
                        audioDTO.setCreateBy(Integer.parseInt(request.getAttribute("id").toString()));
                        audioMapper.saveAudio(audioDTO);
                    }
                    else {
                        audioDTO.setTempUserUUID(request.getAttribute("userId").toString());
                        audioMapper.saveTempAudio(audioDTO);
                    }

                    return audioDTO;
                });
            });
    }
}
