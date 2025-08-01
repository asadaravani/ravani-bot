package com.ravani.ravanibot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gemini")
@Getter
@Setter
public class GeminiConfig {
    private String model;
    private String token;

}
