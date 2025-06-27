package com.ravani.ravanibot.config;

import com.ravani.ravanibot.enums.DocumentType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gemini")
@Getter
@Setter
public class GeminiConfig {
    private String model;
    private String token;
    private final String requestTextPassport = """
You are an expert in document analysis and sworn translation into Russian.

Task:
- Extract all relevant data from the provided passportDto image or scan.
- Translate all into Russian language.
- In Indian, Turkish passports, translate all data into Russian language.
- Gender will be Ж or М.
- Convert all dates to format: DD.MM.YYYY.

Rules:
- Output only a valid JSON object, matching the structure below.
- Do NOT include any explanations, comments, or additional formatting.
- All text must be translated into Russian, except numbers, document codes, and dates.
- If a field is missing or not found, set its value to null (not "...").
- Only in Uzbek passport names, translate the letter J as Ж, not ДЖ.

JSON output format:
{
  "isPassport": true,
  "country_code": "KGZ",
  "number": "PE1234567",
  "issueDate": "01.01.2020",
  "expiryDate": "01.01.2030",
  "issueAuthority": "MIA 213031",
  "person": {
    "surname": "ПЕТРОВ",
    "name": "АЛЕКСАНДР",
    "patronymic": "АЛЕКСАНДРОВИЧ",
    "birth_date": "31.06.2003",
    "gender": "Ж",
    "birth_place": "Кыргызская Республика",
    "personal_number": "21004500200010"
  }
}
""";
    private final String requestTextDriverLicense = "";
    public String getRequestText(DocumentType mode) {
        if (mode == DocumentType.PASSPORT)
            return requestTextPassport;

        return requestTextDriverLicense;
    }
}
