package com.ravani.ravanibot.exceptions;

public class FileDownloadingErrorException extends BaseException {
    public FileDownloadingErrorException(Long chatId, String message) {
        super(chatId, message);
    }
}
