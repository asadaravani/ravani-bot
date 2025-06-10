package com.ravani.ravanibot.exceptions;

public class UnsupportedDocumentException extends BaseException {
    public UnsupportedDocumentException(Long chatId, String message) {
        super(chatId, message);
    }
}
