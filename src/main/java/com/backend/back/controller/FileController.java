package com.backend.back.controller;

import com.backend.back.dto.FilePath;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/file")
public class FileController {

    private final String AUDIO_DIR = FilePath.AUDIO_DIR.getMessage();
    private final String IMAGE_DIR = FilePath.IMAGE_DIR.getMessage();

    @GetMapping("audio/{filename}")
    public ResponseEntity<Resource> getAudio(@PathVariable String filename) throws IOException {
        Path path = Paths.get(AUDIO_DIR + filename);
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        UrlResource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .contentLength(Files.size(path)) // 이 줄 추가!
                .contentType(MediaType.parseMediaType("audio/wav"))
                .body(resource);
    }

    @GetMapping("image/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws IOException {
        Path path = Paths.get(IMAGE_DIR + filename);
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        UrlResource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .contentLength(Files.size(path)) // 이 줄 추가!
                .contentType(MediaType.parseMediaType("image/png"))
                .body(resource);
    }
}
