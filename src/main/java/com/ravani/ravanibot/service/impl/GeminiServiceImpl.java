package com.ravani.ravanibot.service.impl;

import com.google.genai.Client;
import com.google.genai.types.*;
import com.ravani.ravanibot.config.GeminiConfig;
import com.ravani.ravanibot.dtos.DownloadedFile;
import com.ravani.ravanibot.service.GeminiService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeminiServiceImpl implements GeminiService {

    private final GeminiConfig geminiConfig;
    private Client client;

    @PostConstruct
    public void init() {
        this.client = Client.builder().apiKey(geminiConfig.getToken()).build();
    }

    @Override
    public String sendRequest(DownloadedFile file) {
        Content content = getContent(file.bytes(), file.contentType());
        GenerateContentConfig config = getContentConfig();
        GenerateContentResponse response = client.models.generateContent(
                geminiConfig.getModel(), content, config
        );
        return response.text();
    }
    private GenerateContentConfig getContentConfig() {
        return GenerateContentConfig.builder()
                .temperature(0f)
                .responseMimeType("application/json")
                .build();
    }
    @SneakyThrows
    private Content getContent(byte[] file, String contentType) {
        Part imagePart = Part.fromBytes(file,contentType);
        Part textPart = Part.fromText(geminiConfig.getRequestText());
        return Content.builder()
                .parts(List.of(imagePart, textPart) )
                .build();
    }
}
