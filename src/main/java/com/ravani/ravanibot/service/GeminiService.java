package com.ravani.ravanibot.service;

import com.ravani.ravanibot.dtos.DownloadedFile;
import com.ravani.ravanibot.enums.CountryCode;
import com.ravani.ravanibot.enums.DocumentType;

import java.util.List;

public interface GeminiService {
    String sendRequest(List<DownloadedFile> files, DocumentType documentType, CountryCode countryCode, Long chatId);
}
