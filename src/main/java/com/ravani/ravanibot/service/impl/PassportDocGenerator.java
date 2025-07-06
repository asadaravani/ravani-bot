package com.ravani.ravanibot.service.impl;

import com.ravani.ravanibot.constants.SpecialUserDetails;
import com.ravani.ravanibot.dtos.PassportDto;
import com.ravani.ravanibot.dtos.PersonDto;
import com.ravani.ravanibot.enums.CountryCode;
import com.ravani.ravanibot.exceptions.UnsupportedDocumentException;
import com.ravani.ravanibot.utils.DocumentMRZGenerator;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import static com.ravani.ravanibot.service.impl.DocumentServiceImpl.*;

public class PassportDocGenerator {

    static XWPFDocument execute(CountryCode country, PassportDto passportDto, Long chatId) {
        Map<String, String> fields;
        XWPFDocument document;
        if (Objects.equals(chatId, SpecialUserDetails.GULMIRA_CHAT_ID)) {
            return gulmiraExecute(country, passportDto, chatId);
        }
        else if (Objects.equals(chatId, SpecialUserDetails.ASHIM_CHAT_ID)) {
            try {
                return ashimExecute(country, passportDto, chatId);
            }catch (UnsupportedDocumentException e) {
                return gulmiraExecute(country, passportDto, chatId);
            }
        }
        switch (country) {
            case UZB -> {
                document = loadFile(chatId, "bema/uzb_passport.docx");
                fields = mapFieldsUzbNew(passportDto);
                replaceFieldInLayer(document, fields);
                return document;
            }
            case ARM -> {
                document = loadFile(chatId, "bema/arm_passport.docx");
                fields = mapFieldsArm(passportDto);
                replaceFieldInLayer(document, fields);
                return document;
            }
            case KGZ -> {
                if (passportDto.getNumber().contains("AC")) {
                    document = loadFile(chatId, "bema/kgz_passport_old.docx");
                    fields = mapFieldsKgzOld(passportDto);
                    break;
                }
                document = loadFile(chatId, "bema/kgz_passport_new.docx");
                fields = mapFieldsKgzNew(passportDto);
            }
            case TJK -> {
                document = loadFile(chatId, "bema/tjk_passport.docx");
                fields = mapFieldsTjk(passportDto);
            }
            case AZE ->  {
                document = loadFile(chatId, "bema/aze_passport.docx");
                fields = mapFieldsAze(passportDto);
            }
            default -> throw new UnsupportedDocumentException(chatId, "❌Только паспорта UZB, ARM, KGZ, TJK, AZE");
        }
        replaceField(document.getParagraphs(), fields);
        return document;
    }
    private static XWPFDocument gulmiraExecute(CountryCode country, PassportDto passportDto, Long chatId) {
        Map<String, String> fields;
        XWPFDocument document;
        switch (country) {
            case UZB -> {
                if (passportDto.getNumber().startsWith("A")) {
                    document = loadFile(chatId, "gulmira/uzb_passport_old.docx");
                    fields = mapFieldsUzbOld(passportDto);
                    break;
                }
                document = loadFile(chatId,"gulmira/uzb_passport_new.docx");
                fields = mapFieldsUzbNew(passportDto);
            }
            case IND -> {
                document = loadFile(chatId, "gulmira/ind_passport.docx");
                fields = mapFieldsInd(passportDto);
            }
            case TKM -> {
                document = loadFile(chatId, "gulmira/tkm_passport.docx");
                fields = mapFieldsKgzOld(passportDto);
            }
            case TUR -> {
                String gen = detectTurkishPassGen(passportDto.getIssueDate(), passportDto.getIssued_by());
                document = loadFile(chatId, "gulmira/tur_passport_" +  gen + ".docx");
                fields = mapFieldsTur(passportDto);
            }
            case AZE ->  {
                document = loadFile(chatId, "gulmira/aze_passport.docx");
                fields = mapFieldsAzeGul(passportDto);
            }
            case KAZ -> {
                document =  loadFile(chatId, "gulmira/kaz_passport.docx");
                fields = mapFieldsKgzNew(passportDto);
            }
            case KGZ -> {
                document =  loadFile(chatId, "gulmira/kgz_passport_new.docx");
                fields = mapFieldsKgzNewGul(passportDto);
            }
            case TJK -> {
                document = loadFile(chatId, "gulmira/tjk_passport.docx");
                fields = mapFieldsTjk(passportDto);
            }
            default -> throw new UnsupportedDocumentException(chatId, "❌Только паспорта UZB, IND, TUR, TKM, AZE, KAZ, TJK, KGZ");
        }
        replaceFieldInLayer(document, fields);
        return document;
    }

    private static XWPFDocument ashimExecute(CountryCode country, PassportDto dto, Long chatId) {
        Map<String, String> fields;
        XWPFDocument document;
        if (country != CountryCode.KGZ)
            throw new UnsupportedDocumentException(chatId, "❌Пока что только KGZ ()");
        if (dto.getNumber().startsWith("A")) {
            document = loadFile(chatId, "ashim/kgz_passport_old.docx");
            fields = mapFieldsKgzOldAshim(dto);
        } else {
            document = loadFile(chatId, "ashim/kgz_passport_new.docx");
            fields = mapFieldsKgzNewAshim(dto);
        }
        replaceField(document.getParagraphs(), fields);
        replaceFieldInLayer(document, fields);
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
    private static Map<String, String> mapFieldsInd(PassportDto dto) {
        Map<String, String> values = mapFieldsTjk(dto);
        values.put("Поля6", dto.getPerson().birth_place());
        values.put("Поля9", dto.getPlace_of_issue());
        values.put("Поля2", dto.getPerson().name());
        return values;
    }
    private static Map<String, String> mapFieldsTur(PassportDto dto) {
        Map<String, String> values = mapFieldsInd(dto);
        values.put("Поля5", dto.getPerson().personal_number());
        values.put("Поля4", generateFullMonthFormat(dto.getPerson().birth_date()));
        values.put("Поля7", generateFullMonthFormat(dto.getIssueDate()));
        values.put("Поля8", generateFullMonthFormat(dto.getExpiryDate()));
        values.put("Поля9", dto.getIssued_by());
        return values;
    }
    private static Map<String, String> mapFieldsKgzNew(PassportDto dto) {
        Map<String, String> values = mapFieldsTjk(dto);
        values.put("Поля5", dto.getPerson().personal_number());
        values.put("Поля6", translateBirthPlace(dto.getPerson().birth_place()));
        return values;
    }
    private static Map<String, String> mapFieldsKgzNewGul(PassportDto dto) {
        Map<String, String> values = mapFieldsKgzNew(dto);
        String authority = dto.getIssueAuthority().contains("SRS") ?
                dto.getIssueAuthority().replace("SRS", "ГОСУДАРСТВЕННАЯ РЕГИСТРАЦИОННАЯ СЛУЖБА") : dto.getIssueAuthority();
        values.put("Поля9", authority);
        return values;
    }
    private static Map<String, String> mapFieldsKgzOldAshim(PassportDto dto) {
        Map<String, String> values = mapFieldsKgzOld(dto);
        values.put("Ген1", dto.getPerson().gender().startsWith("Ж") ? "ЖЕН" : "МУЖ");
        values.put("Поля4", generateDateFormatWithSpace(dto.getPerson().birth_date()));
        values.put("Поля7", generateDateFormatWithSpace(dto.getIssueDate()));
        values.put("Поля8", generateDateFormatWithSpace(dto.getExpiryDate()));
        values.put("СПАш3", DocumentMRZGenerator.buildMrzFirstLine(dto.getPerson().surname_in_eng(), dto.getPerson().name_in_eng(), dto.getPerson().patronymic_in_eng()));
        values.put("СПАш4", generateMRZ2Ashim(DocumentMRZGenerator.generateMrzSecondLine(dto)));
        return values;
    }
    private static Map<String, String> mapFieldsKgzNewAshim(PassportDto dto) {
        System.err.println(dto.toString());
        String[] passportNumberAshim = generatePassNumAshim(dto.getNumber());

        Map<String, String> values = mapFieldsKgzNew(dto);
        values.put("Поля4", generateDateFormatWithSpace(dto.getPerson().birth_date()));
        values.put("Поля7", generateDateFormatWithSpace(dto.getIssueDate()));
        values.put("Поля8", generateDateFormatWithSpace(dto.getExpiryDate()));
        values.put("СПАш1", passportNumberAshim[0]);
        values.put("СПАш2", passportNumberAshim[1]);
        values.put("СПАш3", DocumentMRZGenerator.buildMrzFirstLine(dto.getPerson().surname_in_eng(), dto.getPerson().name_in_eng(), dto.getPerson().patronymic_in_eng()));
        values.put("СПАш4", generateMRZ2Ashim(DocumentMRZGenerator.generateMrzSecondLine(dto)));
        values.put("СПАш5", generateSpecField5Ashim(dto.getPerson()));
        return values;
    }
    private static Map<String, String> mapFieldsKgzOld(PassportDto dto) {
        Map<String, String> values = mapFieldsKgzNew(dto);
        values.put("Поля2", dto.getPerson().name());
        return values;
    }
    private static Map<String, String> mapFieldsUzbNew(PassportDto dto) {
        Map<String, String> values = mapFieldsKgzOld(dto);
        values.remove("Поля5");
        values.put("Поля3", dto.getPerson().patronymic());
        return values;
    }
    private static Map<String, String> mapFieldsUzbOld(PassportDto dto) {
        Map<String, String> values = mapFieldsUzbNew(dto);
        values.put("СПУз0", dto.getPerson().gender().startsWith("Ж") ? "Женский" : "Мужской");
        values.put("СПУз1", capitalizeFirstLetter(dto.getPerson().nationality()));
        values.put("СПУз2", capitalizeFirstLetter(dto.getPerson().birth_place()));
        values.put("СПУз3", translateAuthority(dto.getIssueAuthority()));
        return values;
    }
    private static Map<String, String> mapFieldsAzeGul(PassportDto dto) {
        Map<String, String> values = mapFieldsKgzNew(dto);
        values.put("Поля6", dto.getPerson().birth_place());
        values.put("Ген1", dto.getPerson().gender());
        values.put("СПАз0", dto.getNumber().chars().mapToObj(c -> (char) c + " ").collect(Collectors.joining()).trim());
        return  values;
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
        Map<String, String> values = mapFieldsUzbNew(dto);
        values.put("СПАр0", generateFullMonthFormat(dto.getPerson().birth_date()));
        values.put("СПАр1", generateFullMonthFormat(dto.getIssueDate()));
        values.put("СПАр2", generateFullMonthFormat(dto.getExpiryDate()));
        return values;
    }

    private static String[] generatePassNumAshim(String passportNumber) {
        String[] result = {"", ""};
        StringBuilder part1 = new StringBuilder(), part2 = new StringBuilder();
        for (int i = 0; i < passportNumber.length(); i++) {
            char ch = passportNumber.charAt(i);
            if (i % 2 == 0) {
                part1.append(ch).append(' ');
            } else {
                part2.append(ch).append(' ');
            }
        }
        result[0] = part1.toString().trim();
        result[1] = part2.toString().trim();

        return result;
    }
    private static String generateMRZ2Ashim(String mrzSecondLine) {
        StringBuilder mrz = new StringBuilder();
        for (int i = 0; i < mrzSecondLine.length(); i++) {
            if(i == 2 || i == 10 || i == 20)
                mrz.append(" ");
            mrz.append(mrzSecondLine.charAt(i));
        }
        return mrz.toString();
    }
    private static String generateSpecField5Ashim(PersonDto dto) {
        String givenNames = dto.patronymic().isEmpty() ? dto.name() : dto.name() + " " + dto.patronymic();
        return dto.personal_number() + "<<" + dto.surname() + " " +  givenNames + "<<" + generateDateFormatWithSpace(dto.birth_date());
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
        if (authority.contains("ХШБ"))
            authority = authority.replace("ХШБ", "ПРС");
        if (authority.equals("MINISTRY OF INTERNAL AFFAIRS"))
            authority = "МИНИСТЕРСТВО ВНУТРЕННИХ ДЕЛ";
        if (authority.equals("STATE PERSONALIZATION CENTRE"))
            return "ГОСУДАРСТВЕННЫЙ ЦЕНТР ПЕРСОНАЛИЗАЦИИ";
        if (authority.contains("SMST"))
            authority = authority.replace("SMST", "ГОСУДАРСТВЕННАЯ МИГРАЦИОННАЯ СЛУЖБА ТУРКМЕНИСТАНА");
        return authority;
    }
    private static String generateDateFormatWithSpace(String date) {
        return date.replace(".", " ");
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
    private static String capitalizeFirstLetter(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
    private static String translateBirthPlace(String birthPlace) {
        if (birthPlace.contains("TKM") ||  birthPlace.contains("ТКМ")) {
            return "ТУРКМЕНИСТАН";
        }
        if (birthPlace.contains("UZB") || birthPlace.contains("УЗБ"))
            return "УЗБЕКИСТАН";
        if (birthPlace.contains("TJK") || birthPlace.contains("ТЖК"))
            return "ТАДЖИКИСТАН";
        if (birthPlace.contains("RUS") || birthPlace.contains("РУС"))
            return "РОССИЯ";
        return birthPlace;
    }
    private static String detectTurkishPassGen(String issueDate, String issued_by) {
        String firstGen = "1st", secondGen = "2nd",  thirdGen = "3rd";
        boolean isRussianEmbassy = issued_by.contains("МОСКВА");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate date = LocalDate.parse(issueDate, formatter);
        if (date.isBefore(LocalDate.of(2018, 4, 1))) {
            return firstGen;
        } else if (date.isBefore(LocalDate.of(2022, 8, 25))) {
            return secondGen;
        } else if (isRussianEmbassy) {
            return secondGen;
        } else {
            return thirdGen;
        }
    }
}
