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
You are an expert in document analysis and sworn translation.

Task:
- Extract all relevant data from the provided passport image or scan.
- Translate all necessary fields from the original language (which may be English or the official language of any country) into proper formal Russian, suitable for sworn translation.
- Normalize and standardize terms as a sworn translator would do, even if the original text is already in Cyrillic.
- Convert all dates to format: DD.MM.YYYY.
- Gender will be Ж or М.

Rules:
- Output only a valid JSON object, matching the structure below.
- Do NOT include any explanations, comments, or additional formatting.
- All text must be translated into Russian, except numbers, document codes, and dates.
- If a field is missing or not found, set its value to null (not "...").
- Only in Uzbek passport names, translate the letter J as Ж, not ДЖ.
- Translate Turkmen and Turkish names properly.
- Return English version of issue authority.

JSON output format:
{
  "isPassport": true,
  "country_code": "IND",
  "number": "PE1234567",
  "issueDate": "01.01.2020",
  "expiryDate": "01.01.2030",
  "issueAuthority": "MIA 213031",
  "place_of_issue": "НЬЮ-ЙОРК",
  "person": {
    "surname": "СЕРГЕЕВ",
    "name": "ВЛАДИСЛАВ",
    "patronymic": "АЛЕКСАНДРОВИЧ",
    "birth_date": "31.06.2003",
    "gender": "М",
    "birth_place": "КЫРГЫЗСКАЯ РЕСПУБЛИКА",
    "personal_number": "21004500200010"
    "nationality": "УЗБЕК",
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
