package com.backend.back.controller;

import com.backend.back.dto.AudioDTO;
import com.backend.back.mapper.AudioMapper;
import com.backend.back.service.AudioService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/audio")
public class AudioController {

    @Autowired
    private AudioService audioService;

    @Autowired
    private AudioMapper audioMapper;

    @ResponseBody
    @GetMapping("/list")
    public List<AudioDTO> getAllAudios() {
        return audioMapper.getAudios();
    }

    @ResponseBody
    @PostMapping("/generate")
    public Object test(@RequestBody HashMap<String, Object> message) {
        JSONParser parser = new JSONParser();
        AudioDTO audioDTO = new AudioDTO();

        return audioService.generateAudioPrompt(message.get("message").toString())
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
                    return audioService.generateAudio(prompt).map(response -> {
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
                        audioMapper.saveAudio(audioDTO);

                        return output.get("file_name").toString();
                    });
                });
    }
}
