package com.ravani.ravanibot.service;

import com.ravani.ravanibot.bot.RavaniBot;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;

@Getter
@Component
public class TelegramSender extends DefaultAbsSender {
    private final String token;

    protected TelegramSender(@Value("${bot.token}") String token, RavaniBot ravaniBot) {
        super(ravaniBot.getOptions(), token);
        this.token = token;
    }
}
