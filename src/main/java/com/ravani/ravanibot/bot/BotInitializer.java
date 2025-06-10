package com.ravani.ravanibot.bot;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@RequiredArgsConstructor
public class BotInitializer {

    private final RavaniBot ravaniBot;

    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(ravaniBot);
            System.out.println("✅ RavaniBot registered and polling started.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
