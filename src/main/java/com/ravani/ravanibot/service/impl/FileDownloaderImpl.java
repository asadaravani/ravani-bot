package com.ravani.ravanibot.service.impl;

import com.ravani.ravanibot.bot.RavaniBot;
import com.ravani.ravanibot.dtos.DownloadedFile;
import com.ravani.ravanibot.exceptions.BotException;
import com.ravani.ravanibot.service.FileDownloader;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;

@Component
@RequiredArgsConstructor
public class FileDownloaderImpl implements FileDownloader {

    private final AbsSender sender;
    private final RestTemplate restTemplate = new RestTemplate();
    private final RavaniBot ravaniBot;

    @Override
    public DownloadedFile downloadFile(Message message) {
        String downloadUrl = getDownloadUrl(message);
        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    downloadUrl,
                    HttpMethod.GET,
                    null,
                    byte[].class
            );
            byte[] bytes = response.getBody();
            if (bytes == null) throw new BotException("Downloaded bytes are null");

            String contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(bytes));
            if (contentType == null || contentType.equals("application/octet-stream"))
                contentType = getContentType(message);

            return DownloadedFile.builder().bytes(bytes).contentType(contentType).build();
        } catch (IOException e) {
            throw new BotException("Could not download the file: IOException");
        }
    }
    private String getFieldId(Message message) {
        String fieldId;
        if (message.hasPhoto()){
            fieldId = message.getPhoto().get(message.getPhoto().size() - 1).getFileId();
        } else if (message.hasDocument()) {
            fieldId = message.getDocument().getFileId();
        }else {
            throw new BotException("Message has no file/document");
        }
        return fieldId;
    }
    @SneakyThrows
    private String getDownloadUrl(Message message) {
        String fieldId = getFieldId(message);
        GetFile getFile = new GetFile(fieldId);
        File file = sender.execute(getFile);
        String filePath = file.getFilePath();

        if (filePath == null || filePath.isBlank()) {
            throw new BotException("FilePath is null or empty");
        }
        String downloadUrl = "https://api.telegram.org/file/bot" + ravaniBot.getBotToken();
        if (!filePath.startsWith("/")) {
            downloadUrl += "/";
        }
        downloadUrl += filePath;
        return downloadUrl;
    }
    private String getContentType(Message message) {
        if (message.hasDocument()) {
            String fileName = message.getDocument().getFileName().toLowerCase();
            if (fileName.endsWith(".pdf")) return "application/pdf";
            else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
            else if (fileName.endsWith(".png")) return "image/png";
            else throw new BotException("Unsupported file type: " + fileName);
        }
        throw new BotException("Cannot detect content type.");
    }
}
