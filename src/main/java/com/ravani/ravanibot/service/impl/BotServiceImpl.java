package com.ravani.ravanibot.service.impl;

import com.ravani.ravanibot.constants.ComRes;
import com.ravani.ravanibot.constants.SpecialUserDetails;
import com.ravani.ravanibot.dtos.DocumentDto;
import com.ravani.ravanibot.dtos.DownloadedFile;
import com.ravani.ravanibot.dtos.PersonDto;
import com.ravani.ravanibot.entities.BotUser;
import com.ravani.ravanibot.enums.CountryCode;
import com.ravani.ravanibot.enums.DocumentType;
import com.ravani.ravanibot.exceptions.AdminPanelException;
import com.ravani.ravanibot.exceptions.FileDownloadingErrorException;
import com.ravani.ravanibot.exceptions.UnsupportedDocumentException;
import com.ravani.ravanibot.service.*;
import com.ravani.ravanibot.utils.CountryCodeExtractor;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
public class BotServiceImpl implements BotService {

    CommandService commandService;
    GeminiService geminiService;
    TelegramSender sender;
    FileDownloader downloader;
    DocumentService documentService;
    Long ownerId = SpecialUserDetails.OWNER_CHAT_ID;
    Integer WAITING_TIME_MILLIS = 5000;
    UserService userService;
    Map<Long, List<DownloadedFile>> tempFiles = new ConcurrentHashMap<>();
    Map<Long, Boolean> isWaiting = new ConcurrentHashMap<>();
    CountryCodeExtractor countryCodeExtractor = new CountryCodeExtractor();

    @Override
    public void handleMessage(Message message) {
        if (message.hasText() && message.getChatId().equals(ownerId)) {
            sendMessageToOwner(commandService.generate(message.getText()));
            return;
        }
        boolean doesUserExist = userService.doesUserExist(message.getChatId());
        if (!doesUserExist) {
            sendMessage(message.getChatId(), ComRes.UNKNOWN_USER + message.getChatId());
            return;
        }
        if ((message.hasDocument() || message.hasPhoto())) {
            handleMedia(message);
        }
    }

    private void sendFile(Long chatId, XWPFDocument document, String fileName, String caption) {
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            document.write(out);
            document.close();

            InputStream inputStream = new ByteArrayInputStream(out.toByteArray());
            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(chatId.toString());
            sendDocument.setDocument(new InputFile(inputStream, fileName));
            if (caption != null)
                sendDocument.setCaption(caption);
            sender.execute(sendDocument);
        } catch (Exception e) {
            throw new FileDownloadingErrorException(chatId, "❌ Не удалось отправить файл. Повторите попытку или обратитесь к админу.");
        }
    }
    @Override
    public void sendMessage(Long chatId, String message){
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(message);
            sender.execute(sendMessage);
        } catch (Exception e) {
            throw new AdminPanelException(e.getMessage());
        }
    }
    @Override
    public void sendMessageToOwner(String message){
        sendMessage(ownerId, message);
    }
    @SneakyThrows
    private void handleMedia(Message message){
        Long chatId = message.getChatId();
        BotUser user = userService.getUserByChatId(chatId);
        DocumentType type = user.getDocumentType();
        DownloadedFile file = downloader.downloadFile(message);
        if (type == DocumentType.PASSPORT) {
            processFile(chatId, List.of(file), type);
            return;
        }
        tempFiles.putIfAbsent(chatId, new ArrayList<>());
        List<DownloadedFile> filesList = tempFiles.get(chatId);
        filesList.add(file);
        if (filesList.size() >= 2) {
            processFile(chatId, filesList.subList(0, 2), type);
            filesList.subList(0, 2).clear();
            return;
        }
        if (Boolean.TRUE.equals(isWaiting.get(chatId))) return;

        isWaiting.put(chatId, true);
        new Thread(() -> {
            try {
                Thread.sleep(WAITING_TIME_MILLIS);
                List<DownloadedFile> currentFiles = tempFiles.get(chatId);
                if (currentFiles.size() == 1) {
                    processFile(chatId, List.of(currentFiles.get(0)), type);
                    currentFiles.clear();
                }
            } catch (InterruptedException ignored) {
            } finally {
                isWaiting.put(chatId, false);
            }
        }).start();
    }
    private void processFile(Long chatId, List<DownloadedFile> files, DocumentType type) {
        CountryCode preCountryCode = countryCodeExtractor.extract(chatId, files.get(0));
        String response = geminiService.sendRequest(files, type,  preCountryCode, chatId);
        DocumentDto dto = documentService.mapToDocumentDto(response, type);
        if(!dto.isDocument())
            throw new UnsupportedDocumentException(chatId, ComRes.getInvalidDocumentResponse(type));
        XWPFDocument xwpfDocument = documentService.fillWordDocument(chatId, dto);
        String captionForWordFile = preCountryCode == null ? ComRes.BAD_QUALITY_FILE : null;
        sendFile(chatId, xwpfDocument, getFileName(chatId, dto.getPerson()), captionForWordFile);
        userService.requestAmountPlusPlus(chatId);
    }
    private String getFileName(Long chatId, PersonDto dto) {
        if(Objects.equals(chatId, SpecialUserDetails.BEMA_CHAT_ID))
            return dto.surname() + ".docx";

        String patronymic = dto.patronymic().isEmpty() ? "" : " " + dto.patronymic();
        return dto.surname() + " "  + dto.name() + patronymic + ".docx";
    }
}
