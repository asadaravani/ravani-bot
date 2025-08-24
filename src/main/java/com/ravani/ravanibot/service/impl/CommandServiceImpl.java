package com.ravani.ravanibot.service.impl;

import com.ravani.ravanibot.constants.ComRes;
import com.ravani.ravanibot.entities.BotUser;
import com.ravani.ravanibot.exceptions.BotException;
import com.ravani.ravanibot.service.BotService;
import com.ravani.ravanibot.service.CommandService;
import com.ravani.ravanibot.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommandServiceImpl implements CommandService {

    BotService botService;
    final UserService userService;

    @Override
    public String generate(String message){
        String[] words = message.split(" ");
        switch (words[0]) {
            case ComRes.ADD -> {
                return handleAddCommand(words);
            }
            case ComRes.REMOVE -> {
                return handleRemoveCommand(words);
            }
            case ComRes.INFO ->  {
                return handleInfoCommand(words);
            }
            case ComRes.MANUAL -> {
                return ComRes.MANUAL_COMMAND_RESPONSE;
            }
            case ComRes.RESET ->  {
                return handleResetCommand(words);
            }
        }
        return ComRes.NO_COMMAND_FOUND;
    }
    private String handleAddCommand(String[] words){
        Long chatId = Long.parseLong(words[1]);
        userService.addUser(chatId, words[2]);
        botService.sendMessage(chatId, ComRes.NEW_USER_GREETING);
        return ComRes.POSITIVE_RESPONSE;
    }
    private String handleResetCommand(String[] words){
        userService.resetRequestAmount(words[1]);
        return ComRes.POSITIVE_RESPONSE;
    }
    private String handleRemoveCommand(String[] words){
        if (words.length != 2){
            throw new BotException(ComRes.INVALID_COMMAND);
        }
        BotUser user = userService.getUserByName(words[1]);
        userService.deleteUser(user.getChatId());
        return ComRes.POSITIVE_RESPONSE;
    }

    private String handleInfoCommand(String[] words){
        if (words.length > 2){
            throw new BotException(ComRes.INVALID_COMMAND);
        }
        if (words.length == 2){
            BotUser user = userService.getUserByName(words[1]);
            return userToString(user);
        }
        StringBuilder response = new StringBuilder();
        List<BotUser> users = userService.getAllUsers();
        for (int i = 0; i < users.size(); i++){
            response.append(i + 1).append(". ").append(userToString(users.get(i)));
        }
        return response.toString();
    }
    private String userToString(BotUser user){
        return user.getChatId() + " " + user.getName() + " made " + user.getRequestAmount() + " reqs\n";
    }

    @Lazy
    @Autowired
    public void setBotService(BotService botService) {
        this.botService = botService;
    }
}
