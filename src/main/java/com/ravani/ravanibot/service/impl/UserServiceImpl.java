package com.ravani.ravanibot.service.impl;

import com.ravani.ravanibot.entities.BotUser;
import com.ravani.ravanibot.enums.DocumentType;
import com.ravani.ravanibot.exceptions.AdminPanelException;
import com.ravani.ravanibot.repos.UserRepository;
import com.ravani.ravanibot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<BotUser> getAllUsers() {
        List<BotUser> users = repository.findAll();
        if (users.isEmpty()) {
            throw new AdminPanelException("DB is empty❗");
        }
        return users;
    }

    @Override
    public BotUser getUserByChatId(Long chatId) {
        return repository.findByChatId(chatId).orElseThrow(() -> new AdminPanelException("👔User not found with chatId: "  + chatId + "❗"));
    }

    @Override
    public BotUser getUserByName(String name) {
        return repository.findByName(name).orElseThrow(() -> new AdminPanelException("👔User not found with name: "  + name + "❗"));
    }

    @Override
    public void addUser(Long chatId, String name) {
        BotUser user = new BotUser();
        user.setChatId(chatId);
        user.setName(name);
        user.setDocumentType(DocumentType.PASSPORT);
        repository.save(user);
    }

    @Override
    public void resetRequestAmount(String name) {
        BotUser user = getUserByName(name);
        user.setRequestAmount(BigInteger.ZERO);
        repository.save(user);
    }

    @Override
    public void requestAmountPlusPlus(Long chatId) {
        BotUser user = getUserByChatId(chatId);
        if (user.getRequestAmount() == null) {
            user.setRequestAmount(BigInteger.ZERO);
        }
        user.setRequestAmount(user.getRequestAmount().add(BigInteger.ONE));
        repository.save(user);
    }

    @Override
    public void deleteUser(Long chatId) {
        BotUser user = getUserByChatId(chatId);
        repository.delete(user);
    }

    @Override
    public boolean doesUserExist(Long chatId) {
        try {
            BotUser user = getUserByChatId(chatId);
            return true;
        }catch (AdminPanelException e) {
            return false;
        }
    }

    @Override
    public void setTranslationMode(Long chatId, DocumentType documentType) {
        BotUser user = getUserByChatId(chatId);
        user.setDocumentType(documentType);
        repository.save(user);
    }
}
