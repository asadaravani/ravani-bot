package com.ravani.ravanibot.service.impl;

import com.ravani.ravanibot.dtos.Passport;
import java.util.HashMap;
import java.util.Map;

public class DocumentServiceUtil {

    static Map<String, String> mapFieldsTjk(Passport passport) {
        String name = passport.person().patronymic() == null ? passport.person().name().toUpperCase()
                : passport.person().name().toUpperCase() + " " + passport.person().patronymic().toUpperCase();
        Map<String, String> values = new HashMap<>();
        values.put("Поля0", passport.number());
        values.put("Поля1", passport.person().surname().toUpperCase());
        values.put("Поля2", name);
        values.put("Поля4", passport.person().birth_date());
        values.put("Поля7", passport.issueDate());
        values.put("Поля8", passport.expiryDate());
        values.put("Поля9", translateAuthority(passport.issueAuthority().toUpperCase()));
        values.put("Ген1", passport.person().gender().toUpperCase());
        return values;
    }
    static Map<String, String> mapFieldsKgzNew(Passport passport) {
        Map<String, String> values = mapFieldsTjk(passport);
        String personal_number = passport.person().personal_number() != null ? passport.person().personal_number().toUpperCase() : "";
        values.put("Поля5", personal_number);
        values.put("Поля6", passport.person().birth_place().toUpperCase());
        return values;
    }
    static Map<String, String> mapFieldsKgzOld(Passport passport) {
        Map<String, String> values = mapFieldsKgzNew(passport);
        values.put("Поля2", passport.person().name().toUpperCase());
        return values;
    }
    static Map<String, String> mapFieldsUzb(Passport passport) {
        Map<String, String> values = mapFieldsKgzOld(passport);
        values.remove("Поля5");
        values.put("Поля3", passport.person().patronymic().toUpperCase());
        return values;
    }
    static Map<String, String> mapFieldsAze(Passport passport) {
        Map<String, String> values = mapFieldsKgzNew(passport);
        values.put("Поля6", stringX2SlashSpace(passport.person().birth_place().toUpperCase()));
        values.put("Ген1", stringX2Slash(passport.person().gender().toUpperCase()));
        values.put("СПАз0", passport.person().name().toUpperCase());
        values.put("СПАз1", translateAuthority(passport.issueAuthority().toUpperCase()) + "/");
        return values;
    }
    static Map<String, String> mapFieldsArm(Passport passport) {
        Map<String, String> values = mapFieldsUzb(passport);
        values.put("СПАр0", generateFullMonthFormat(passport.person().birth_date()));
        values.put("СПАр1", generateFullMonthFormat(passport.issueDate()));
        values.put("СПАр2", generateFullMonthFormat(passport.expiryDate()));
        return values;
    }
    private static String stringX2Slash(String string) {
        return string + "/" + string;
    }
    private static String stringX2SlashSpace(String string) {
        return string + " / " + string;
    }

    private static String translateAuthority(String authority) {
        if (authority.contains("SRS"))
            authority = authority.replace("SRS", "ГРС");
        if (authority.contains("MIA"))
            authority = authority.replace("MIA", "МВД");
        if (authority.equalsIgnoreCase("MINISTRY OF INTERNAL AFFAIRS"))
            authority = "МИНИСТЕРСТВО ВНУТРЕННИХ ДЕЛ";
        return authority;
    }

    private static String generateFullMonthFormat(String date) {
        String[] arr = date.split("\\.");
        String month = "null";
        switch (arr[1]) {
            case "01" -> month = "ЯНВАРЯ";
            case "02" -> month = "ФЕВРАЛЯ";
            case "03" -> month = "МАРТА";
            case "04" -> month = "АПРЕЛЯ";
            case "05" -> month = "МАЯ";
            case "06" -> month = "ИЮНЯ";
            case "07" -> month = "ИЮЛЯ";
            case "08" -> month = "АВГУСТА";
            case "09" -> month = "СЕНТЯБРЯ";
            case "10" -> month = "ОКТЯБРЯ";
            case "11" -> month = "НОЯБРЯ";
            case "12" -> month = "ДЕКАБРЯ";
        }
        return arr[0] + " " + month + " "  + arr[2];
    }
}
