package com.ravani.ravanibot.service;

import com.ravani.ravanibot.entities.BotUser;
import com.ravani.ravanibot.enums.DocumentType;

import java.util.List;

public interface UserService {
    List<BotUser> getAllUsers();

    BotUser getUserByChatId(Long chatId);

    BotUser getUserByName(String name);


    void addUser(Long chatId, String name);

    void resetRequestAmount(String name);

    void requestAmountPlusPlus(Long chatId);

    void deleteUser(Long chatId);

    boolean doesUserExist(Long chatId);

    void setTranslationMode(Long chatId, DocumentType documentType);
}
