package com.backend.back.service;

import com.backend.back.properties.GeminiProperties;
import com.backend.back.mapper.UserMapper;
import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class AudioService {

    private final GeminiProperties properties;
    private final WebClient webClient;

    @Autowired
    private UserMapper userMapper;

    public AudioService(GeminiProperties properties) {
        this.properties = properties;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getApiBaseUrl())
                .defaultHeader("Content-Type", "application/json")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build();
    }


    public Mono<String> generateAudioPrompt(String userInput) {
        String uri = properties.getApiTextUrl() + properties.getApiKey();
        Map<String, Object> part_sys = Map.of("text", "You are a professional Prompt Engineer for generative audio models like Stable Audio.\n" +
                "Your role is to receive Korean-language input from the user that describes a visual scene or image they are generating. These are not direct audio requests but image prompts. Your task is to:\n" +
                "Interpret the scene — Read the image description and extract key situational and environmental elements that would naturally produce sound. Focus on materials, movements, energy, atmosphere, environment, and actions depicted.\n" +
                "Convert visuals into sound — Infer what this scene would sound like in reality or in a cinematic audio space. Identify implied sound sources (e.g., footsteps on ice, wings flapping, flames crackling, magical auras humming, distant winds).\n" +
                "Compose a detailed audio prompt — Based on your interpretation, write a single, fluent English prompt that follows Stable Audio’s best practices:\n" +
                "Prompt Writing Guidelines:\n" +
                "Use specific detail (genre, mood, instruments, textures, events, ambience).\n" +
                "Set the mood using a combination of musical and emotional terms (e.g., “ominous and orchestral”, “euphoric and ethereal”).\n" +
                "Choose instruments (and describe them with adjectives if relevant, e.g., “deep metallic drums”, “glassy pads”).\n" +
                "Optionally set the BPM if tempo is implied (e.g., action, calm meditation).\n" +
                "Focus entirely on audible elements. Do not include purely visual descriptions.\n" +
                "Output must be in English only—do not translate or explain.\n" +
                "Your output should be only the final audio prompt in fluent English. Do not return explanations or commentary.");

        Map<String, Object> part_con = Map.of("text", userInput);

        Map<String, Object> body = Map.of(
                "system_instruction", Map.of("parts", List.of(part_sys)),
                "contents", List.of(Map.of("parts", List.of(part_con))));

        return webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> generateAudio(String prompt) {
        WebClient webclient = WebClient.builder()
                .defaultHeader("Content-Type", "application/json")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofSeconds(90)) // 응답 타임아웃
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000) // 연결 타임아웃 (10초)
                ))
                .build();

        String uri = "http://localhost:80/ai/generate_audio";

        Map<String, Object> body = Map.of(
                "prompt", prompt);

        return webclient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);
    }
}
