package com.ravani.ravanibot.service;

import com.ravani.ravanibot.dtos.DownloadedFile;

public interface GeminiService {
    String sendRequest(DownloadedFile file);
}
