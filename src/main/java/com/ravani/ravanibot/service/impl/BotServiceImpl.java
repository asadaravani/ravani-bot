package com.ravani.ravanibot.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ravani.ravanibot.dtos.DownloadedFile;
import com.ravani.ravanibot.dtos.Passport;
import com.ravani.ravanibot.exceptions.FileDownloadingErrorException;
import com.ravani.ravanibot.exceptions.UnsupportedDocumentException;
import com.ravani.ravanibot.service.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class BotServiceImpl implements BotService {

    private final GeminiService geminiService;
    private final TelegramSender sender;
    private final FileDownloader downloader;
    private final DocumentService documentService;
    private final Long ownerId = 735283091L;

    @Override
    public void handleMessage(Message message) {
        if (message.hasDocument() || message.hasPhoto()){
            handleMedia(message);
            return;
        }
    }

    private void sendFile(Long chatId, XWPFDocument document, String fileName) {
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            document.write(out);
            document.close();

            InputStream inputStream = new ByteArrayInputStream(out.toByteArray());
            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(chatId.toString());
            sendDocument.setDocument(new InputFile(inputStream, fileName));
            sender.execute(sendDocument);
        } catch (Exception e) {
            throw new FileDownloadingErrorException(chatId, "❌ Не удалось отправить файл. Повторите попытку или обратитесь к админу.");
        }

    }
    @SneakyThrows
    @Override
    public void sendMessage(Long chatId, String message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sender.execute(sendMessage);
    }
    @Override
    public void sendMessageToOwner(String message){
        sendMessage(ownerId, message);
    }
    @SneakyThrows
    private void handleMedia(Message message){
        DownloadedFile media = downloader.downloadFile(message);
        String response = geminiService.sendRequest(media);
        System.err.println(response);
        ObjectMapper mapper = new ObjectMapper();
        Passport passport = mapper.readValue(response, Passport.class);
        if(passport.isPassport() == false){
            throw new UnsupportedDocumentException(message.getChatId(), "❌Это не паспорт!. Не тратьте наши ресурсы впустую.");
        }
        System.out.println(passport);

        XWPFDocument xwpfDocument = documentService.fillWordDocument(message.getChatId(), passport);
        sendFile(message.getChatId(), xwpfDocument, passport.person().surname() + ".docx");
    }

}
