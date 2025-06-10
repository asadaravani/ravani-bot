package com.ravani.ravanibot.exceptions;

import com.ravani.ravanibot.service.BotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final BotService botService;
    public void handle(BaseException e) {
        botService.sendMessage(e.getChatId(), e.getMessage());
    }
}
