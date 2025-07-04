package com.ravani.ravanibot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "tesseract")
@Getter
@Setter
public class TesseractConfig {

    private String dataPath;
}
