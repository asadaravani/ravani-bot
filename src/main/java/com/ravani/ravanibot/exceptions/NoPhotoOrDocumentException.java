package com.ravani.ravanibot.exceptions;

public class NoPhotoOrDocumentException extends BaseException{

    public NoPhotoOrDocumentException(Long chatId, String message) {
        super(chatId, message);
    }
}
