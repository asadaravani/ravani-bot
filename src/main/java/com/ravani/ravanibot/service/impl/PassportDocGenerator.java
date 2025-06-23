package com.ravani.ravanibot.service.impl;

import com.ravani.ravanibot.dtos.PassportDto;
import com.ravani.ravanibot.enums.Countries;
import com.ravani.ravanibot.exceptions.UnsupportedDocumentException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import java.util.HashMap;
import java.util.Map;
import static com.ravani.ravanibot.service.impl.DocumentServiceImpl.*;

public class PassportDocGenerator {

    static XWPFDocument execute(Countries country, PassportDto passportDto, Long chatId) {
        Map<String, String> fields;
        XWPFDocument document;
        switch (country) {
            case UZB -> {
                document = loadFile(chatId, "docs/uzb_passport.docx");
                fields = PassportDocGenerator.mapFieldsUzb(passportDto);
                replaceFieldInLayer(document, fields);
                return document;
            }
            case ARM -> {
                document = loadFile(chatId, "docs/arm_passport.docx");
                fields = PassportDocGenerator.mapFieldsArm(passportDto);
                replaceFieldInLayer(document, fields);
                return document;
            }
            case KGZ -> {
                if (passportDto.getNumber().contains("AC")) {
                    document = loadFile(chatId, "docs/kgz_passport_old.docx");
                    fields = PassportDocGenerator.mapFieldsKgzOld(passportDto);
                    break;
                }
                document = loadFile(chatId, "docs/kgz_passport_new.docx");
                fields = PassportDocGenerator.mapFieldsKgzNew(passportDto);
            }
            case TJK -> {
                document = loadFile(chatId, "docs/tjk_passport.docx");
                fields = PassportDocGenerator.mapFieldsTjk(passportDto);
            }
            case AZE ->  {
                document = loadFile(chatId, "docs/aze_passport.docx");
                fields = PassportDocGenerator.mapFieldsAze(passportDto);
            }
            default -> throw new UnsupportedDocumentException(chatId, "Definitely unexpected exception in the DocService:switch. Contact _admin_");
        }
        replaceField(document.getParagraphs(), fields);
        return document;
    }

    private static Map<String, String> mapFieldsTjk(PassportDto dto) {
        String name = dto.getPerson().patronymic().isEmpty() ? dto.getPerson().name()
                : dto.getPerson().name() + " " + dto.getPerson().patronymic();
        Map<String, String> values = new HashMap<>();
        values.put("Поля0", dto.getNumber());
        values.put("Поля1", dto.getPerson().surname());
        values.put("Поля2", name);
        values.put("Поля4", dto.getPerson().birth_date());
        values.put("Поля7", dto.getIssueDate());
        values.put("Поля8", dto.getExpiryDate());
        values.put("Поля9", translateAuthority(dto.getIssueAuthority()));
        values.put("Ген1", dto.getPerson().gender());
        return values;
    }
    private static Map<String, String> mapFieldsKgzNew(PassportDto dto) {
        Map<String, String> values = mapFieldsTjk(dto);
        values.put("Поля5", dto.getPerson().personal_number());
        values.put("Поля6", dto.getPerson().birth_place());
        return values;
    }
    private static Map<String, String> mapFieldsKgzOld(PassportDto dto) {
        Map<String, String> values = mapFieldsKgzNew(dto);
        values.put("Поля2", dto.getPerson().name());
        return values;
    }
    private static Map<String, String> mapFieldsUzb(PassportDto dto) {
        Map<String, String> values = mapFieldsKgzOld(dto);
        values.remove("Поля5");
        values.put("Поля3", dto.getPerson().patronymic());
        return values;
    }
    private static Map<String, String> mapFieldsAze(PassportDto dto) {
        Map<String, String> values = mapFieldsKgzNew(dto);
        values.put("Поля6", stringX2SlashSpace(dto.getPerson().birth_place()));
        values.put("Ген1", stringX2Slash(dto.getPerson().gender()));
        values.put("СПАз0", dto.getPerson().name());
        values.put("СПАз1", translateAuthority(dto.getIssueAuthority()) + "/");
        return values;
    }
    private static Map<String, String> mapFieldsArm(PassportDto dto) {
        Map<String, String> values = mapFieldsUzb(dto);
        values.put("СПАр0", generateFullMonthFormat(dto.getPerson().birth_date()));
        values.put("СПАр1", generateFullMonthFormat(dto.getIssueDate()));
        values.put("СПАр2", generateFullMonthFormat(dto.getExpiryDate()));
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
        if (authority.contains("PSK"))
            authority = authority.replace("PSK", "ГЦП");
        if (authority.equals("MINISTRY OF INTERNAL AFFAIRS"))
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
