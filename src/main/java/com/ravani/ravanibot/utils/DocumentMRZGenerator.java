package com.ravani.ravanibot.utils;

import com.ravani.ravanibot.dtos.PassportDto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DocumentMRZGenerator {
    public static String buildMrzFirstLine(String surname_eng, String name_eng, String patronymic_eng) {
        StringBuilder mrz = new StringBuilder();
        String names = patronymic_eng.isEmpty() ? name_eng : name_eng + "<" + patronymic_eng;
        mrz.append("P<KGZ");
        mrz.append(surname_eng).append("<<");
        mrz.append(names);

        while (mrz.length() < 44) {
            mrz.append('<');
        }

        return mrz.substring(0, 44);
    }
    private static final String NATIONALITY = "KGZ";

    public static String generateMrzSecondLine(PassportDto dto) {
        char sex = dto.getPerson().gender().equalsIgnoreCase("М") ? 'M' :
                dto.getPerson().gender().equalsIgnoreCase("Ж") ? 'F' : '<';

        String passportNumber = padRight(dto.getNumber(), 9, '<');
        String passportCheck = calculateCheckDigit(passportNumber);

        String birthDate = formatToMrzDate(dto.getPerson().birth_date());
        String birthCheck = calculateCheckDigit(birthDate);

        String expiryDate = formatToMrzDate(dto.getExpiryDate());
        String expiryCheck = calculateCheckDigit(expiryDate);

        String personalNumberField = padRight(dto.getPerson().personal_number() != null ? dto.getPerson().personal_number() : "", 14, '<');
        String personalNumberCheck = calculateCheckDigit(personalNumberField);

        String sexField = (sex == 'M' || sex == 'F') ? String.valueOf(sex) : "<";

        String finalCheckInput = passportNumber + passportCheck +
                birthDate + birthCheck +
                expiryDate + expiryCheck +
                personalNumberField + personalNumberCheck;

        String finalCheckDigit = calculateCheckDigit(finalCheckInput);

        return passportNumber + passportCheck +
                NATIONALITY +
                birthDate + birthCheck +
                sexField +
                expiryDate + expiryCheck +
                personalNumberField + personalNumberCheck +
                finalCheckDigit;
    }

    private static String formatToMrzDate(String input) {
        try {
            DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            DateTimeFormatter mrzFormat = DateTimeFormatter.ofPattern("yyMMdd");
            return LocalDate.parse(input, inputFormat).format(mrzFormat);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Invalid date: " + input);
        }
    }


    private static String padRight(String value, int length, char padChar) {
        StringBuilder result = new StringBuilder(value != null ? value : "");
        while (result.length() < length) result.append(padChar);
        return result.substring(0, length);
    }

    private static String calculateCheckDigit(String input) {
        int[] weights = {7, 3, 1};
        int sum = 0;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            int value;

            if (c >= '0' && c <= '9') {
                value = c - '0';
            } else if (c >= 'A' && c <= 'Z') {
                value = c - 'A' + 10;
            } else if (c == '<') {
                value = 0;
            } else {
                value = 0; // fallback
            }

            sum += value * weights[i % 3];
        }

        return String.valueOf(sum % 10);
    }

}
