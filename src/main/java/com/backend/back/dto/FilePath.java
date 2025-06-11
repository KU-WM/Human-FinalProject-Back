package com.backend.back.dto;

import lombok.Getter;

@Getter
public enum FilePath {
    AUDIO_DIR("C:/Users/Ku/Server/ku9907/audio/"),
    IMAGE_DIR("C:/Users/Ku/Server/ku9907/image/");

    private final String message;

    FilePath(String message) {
        this.message = message;
    }
}
