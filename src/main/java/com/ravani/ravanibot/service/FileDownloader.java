package com.ravani.ravanibot.service;

import com.ravani.ravanibot.dtos.DownloadedFile;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface FileDownloader {
    @SneakyThrows
    DownloadedFile downloadFile(Message message);
}
