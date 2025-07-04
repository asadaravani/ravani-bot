package com.ravani.ravanibot.service.impl;

import com.google.genai.Client;
import com.google.genai.types.*;
import com.ravani.ravanibot.config.GeminiConfig;
import com.ravani.ravanibot.constants.GeminiRequests;
import com.ravani.ravanibot.dtos.DownloadedFile;
import com.ravani.ravanibot.enums.CountryCode;
import com.ravani.ravanibot.enums.DocumentType;
import com.ravani.ravanibot.service.GeminiService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
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
    public String sendRequest(List<DownloadedFile> files, DocumentType type, CountryCode countryCode) {
        Content content = getContent(files, type, countryCode);
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
    private Content getContent(List<DownloadedFile> files, DocumentType documentType, CountryCode countryCode) {
        List<Part>  parts = new ArrayList<>();
        files.forEach(file -> parts.add(Part.fromBytes(file.bytes(),  file.contentType())));
        parts.add(Part.fromText(GeminiRequests.getRequestText(documentType, countryCode)));
        return Content.builder()
                .parts(parts)
                .build();
    }
}
