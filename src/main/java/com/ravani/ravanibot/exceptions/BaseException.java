package com.ravani.ravanibot.exceptions;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private final Long chatId;
    public BaseException(Long chatId, String message) {
        super(message);
        this.chatId = chatId;
    }
}
