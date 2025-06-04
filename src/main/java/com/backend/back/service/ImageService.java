package com.backend.back.service;

import com.backend.back.config.GeminiProperties;
import com.backend.back.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;


@Service
public class ImageService {

    private final GeminiProperties properties;
    private final WebClient webClient;

    @Autowired
    private UserMapper userMapper;

    public ImageService(GeminiProperties properties) {
        this.properties = properties;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getApiBaseUrl())
                .defaultHeader("Content-Type", "application/json")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build();
    }


    public Mono<String> generatePrompt(String userInput) {
        String uri = properties.getApiTextUrl() + properties.getApiKey();
        Map<String, Object> part_sys = Map.of("text", "You are a prompt engineer specialized in creating keyword-rich prompts for Stable Diffusion. Use English keywords only. Convert a simple image idea into a single, well-structured prompt made of concise, comma-separated visual keywords. Focus on realism, high detail, lighting, mood, and scene elements. Do not write full sentences, lists, or explanations. Only return one prompt per input.");

        Map<String, Object> part_con = Map.of("text", userInput);

        Map<String, Object> body = Map.of(
                "system_instruction", Map.of("parts",List.of(part_sys)),
                "contents", List.of(Map.of("parts", List.of(part_con))));

        return webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> generateImage(String prompt) {
        String uri = properties.getApiImageUrl() + properties.getApiKey();

        Map<String, Object> part_con = Map.of("text", prompt);

        Map<String, Object> responseModalities = Map.of("responseModalities", List.of("text", "IMAGE"));

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of("parts", List.of(part_con))),
                "generationConfig", responseModalities);

        return webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);
    }


    public Mono<String> getChat(String prompt) {
        String uri = properties.getGemma3ChatUrl() + properties.getApiKey();

        String logfile = userMapper.loadDialogue(1);
        if (logfile == null) {
            logfile = "{\"dialogues\": []}";
            userMapper.saveDialogue(logfile);
        }

        String base64Encoded = Base64.getEncoder().encodeToString(logfile.getBytes(StandardCharsets.UTF_8));

        Map<String, Object> part = Map.of("text", prompt);

        Map<String, Object> inline_data = Map.of("mime_type", "application/json", "data", base64Encoded);

        Map<String, Object> part2 = Map.of("file_data", inline_data);

        Map<String, Object> parts = Map.of("parts", List.of(part, part2));
        Map<String, Object> contents = Map.of("contents", List.of(parts));


        return webClient.post()
                .uri(uri)
                .header("Authorization", "Bearer " + properties.getGemma3Api())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(contents)
                .retrieve()
                .bodyToMono(String.class);
    }

}
