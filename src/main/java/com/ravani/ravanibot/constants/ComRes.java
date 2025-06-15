package com.ravani.ravanibot.constants;

public class ComRes {

    public static final String ADD = "add";
    public static final String REMOVE = "remove";
    public static final String INFO = "info";
    public static final String MANUAL = "man";

    public static final String MANUAL_COMMAND_RESPONSE =
            "*"+ INFO + "* returns info about all users. User _<name>_ args to get specific user's info\n\n"
                    + "*"  + ADD + "* _<chatId>_ and _<name>_.\n\n"
                    + "*"  + REMOVE + "* _<name>_.\n\n";

    public static final String INVALID_COMMAND = "Invalid command⚠️, your majesty🙇‍♂️";
    public static final String NO_COMMAND_FOUND = "Command me🙇‍, your majesty️👑";
    public static final String POSITIVE_RESPONSE = "It is ✅, your grace👑";

    public static final String UNKNOWN_USER = "Hi, сообщите моему владельцу @asadaravani, чтобы получить доступ к моему сервису.\n"
            + "Передайте ему наш chat ID: ";
}
