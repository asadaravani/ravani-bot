package com.ravani.ravanibot.constants;

import com.ravani.ravanibot.enums.DocumentType;

public class ComRes {

    public static final String ADD = "add";
    public static final String REMOVE = "remove";
    public static final String INFO = "info";
    public static final String RESET = "reset";
    public static final String MANUAL = "man";

    public static final String MANUAL_COMMAND_RESPONSE =
            INFO + " returns info about all users.\n\n"
                    + INFO + " <name> args to get specific user's info\n\n"
                    + ADD + " <chatId> and <name>.\n\n"
                    + REMOVE + " <name>.\n\n"
                    + RESET + " <name>.\n\n"
                    + "version 2.4.01";

    public static final String INVALID_COMMAND = "Invalid command⚠️";
    public static final String NO_COMMAND_FOUND = "Hello, boss🫡";
    public static final String POSITIVE_RESPONSE = "✅";

    public static final String UNKNOWN_USER = "Hi, сообщите моему владельцу @asadaravani, чтобы получить доступ к моему сервису.\n"
            + "Передайте ему наш chat ID: ";
    public static final String MODE_CHANGE_RESPONSE = "🔄Режим перевода: ";
    public static final String NEW_USER_GREETING = "👋🏼Привет! Я бот-переводчик\n"
            + "Вы можете отправлять JPG, JPEG, PDF и другие файлы.\n"
            + "Пожалуйста, отправляйте файлы без лишних страниц и ненужных данных, чтобы получить ответ быстрее.\n"
            + "⚠️Админ видит, сколько запросов вы отправляли.";

    private static final String INVALID_DOCUMENT = "!. Не тратьте наши ресурсы впустую.";
    public static String getInvalidDocumentResponse(DocumentType documentType) {
        return "😡 Это не " + documentType.toString() + INVALID_DOCUMENT;
    }
    public static String TOO_LARGE_FILE = "😭Файл слишком большой, вы можете сделать скриншот и отправить снова.\n Главное — чтобы были видны все буквы, даже маленькие🫰";
    public static String BAD_QUALITY_FILE = "Качество 👎🏻  или перевёрнутый скан.";
}
