package com.ravani.ravanibot.service;

import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface BotService {
    void handleMessage(Message message);

    @SneakyThrows
    void sendMessage(Long chatId, String message);

    void sendMessageToOwner(String message);
}
