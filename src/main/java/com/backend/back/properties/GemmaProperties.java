package com.backend.back.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gemma")
@Data
public class GemmaProperties {
    private String gemma3ChatUrl;
    private String gemma3Api;
    private String gemma3Url;
}
