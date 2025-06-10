package com.ravani.ravanibot.bot;

import com.ravani.ravanibot.exceptions.BaseException;
import com.ravani.ravanibot.exceptions.GlobalExceptionHandler;
import com.ravani.ravanibot.service.BotService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class RavaniBot implements LongPollingBot {
    private GlobalExceptionHandler globalExceptionHandler;
    private BotService service;
    private final DefaultBotOptions botOptions;


    @Value("${bot.username}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    public RavaniBot() {
        this.botOptions = new DefaultBotOptions();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            try{
                service.handleMessage(update.getMessage());
            }catch (BaseException e){
                globalExceptionHandler.handle(e);
            }
        }
    }

    @Override
    public DefaultBotOptions getOptions() {
        return this.botOptions;
    }

    @Override
    public void clearWebhook() {
        try {
            URL url = new URL("https://api.telegram.org/bot" + getBotToken() + "/deleteWebhook");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            System.out.println("Webhook clear response: " + responseCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Autowired
    @Lazy
    public void setService(BotService service) {
        this.service = service;
    }

    @Autowired
    @Lazy
    public void setGlobalExceptionHandler(GlobalExceptionHandler globalExceptionHandler) {
        this.globalExceptionHandler = globalExceptionHandler;
    }
}
