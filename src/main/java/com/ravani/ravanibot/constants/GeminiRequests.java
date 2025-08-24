package com.ravani.ravanibot.constants;

import com.ravani.ravanibot.enums.CountryCode;
import com.ravani.ravanibot.enums.DocumentType;
import com.ravani.ravanibot.exceptions.BotException;

import java.util.Objects;

public class GeminiRequests {
    private static final String undetectedCountry = """
You are an expert in document analysis and sworn translation.

Task:
- Extract all relevant data from the provided passport image or scan.
- Translate all necessary fields from the original language (which may be English or the official language of any country) into proper formal Russian, suitable for sworn translation.
- Normalize and standardize terms as a sworn translator would do, even if the original text is already in Cyrillic.
- Convert all dates to format: DD.MM.YYYY.
- Gender will be Ж or М. Translate birth place into russian
- Return English version of issue authority.

Rules:
- Output only a valid JSON object, matching the structure below.
- Do NOT include any explanations, comments, or additional formatting.
- All text must be translated into Russian, except numbers, document codes, and dates and issueAuthority.
- If a field is missing or not found, set its value to null (not "...").
- Only in Uzbek passport names, translate the letter J as Ж, not ДЖ.

JSON output format:
{
  "isPassport": true,
  "country_code": "UZB",
  "number": "FA1234567",
  "issueDate": "01.01.2020",
  "expiryDate": "01.01.2030",
  "issueAuthority": "MIA 213031",
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
    private static final String PHL_PASSPORT = """
You are an expert in document analysis and sworn translation.

Task:
- Extract all relevant data from the provided passport image or scan.
- Translate all fields into proper formal Russian, suitable for sworn translation.
- Convert all dates to format: DD.MM.YYYY.
- Gender will be Ж or М.
- Translate issue authority properly. "DFA" translates as ДЕПАРТАМЕНТ ИНОСТРАННЫХ ДЕЛ".
- Do not leave any field without translation!

Rules:
- Output only a valid JSON object, matching the structure below.
- Do NOT include any explanations, comments, or additional formatting.
- All text must be translated into Russian, except numbers, document codes, and dates.
- If a field is missing or not found, set its value to null (not "...").

JSON output format:
{
  "isPassport": true,
  "country_code": "PHL",
  "number": "P1234567A",
  "issueDate": "01.01.2020",
  "expiryDate": "01.01.2030",
  "issueAuthority": "ДЕПАРТАМЕНТ ИНОСТРАННЫХ ДЕЛ МАНИЛА",
  "person": {
    "surname": "ДЕЛА КРУЗ",
    "given_names": "МАРИА",
    "middle_name": "САНТОС",
    "birth_date": "31.06.2003",
    "gender": "Ж",
    "birth_place": "МАНИЛА",
  }
}
""";

    private static final String KGZ_PASSPORT_ASHIM = """
You are an expert in document analysis and sworn translation.

Task:
- Extract all relevant data from the provided passport image or scan.
- Translate all necessary fields from the original language (which may be English or the official language of any country) into proper formal Russian, suitable for sworn translation.
- Normalize and standardize terms as a sworn translator would do, even if the original text is already in Cyrillic.
- Convert all dates to format: DD.MM.YYYY.
- Gender will be Ж or М. Translate birth place into russian
- In fields surname_in_eng, name_in_eng, patronymic_in_eng return version of names.
- The mrz_second_line field should contain the complete second line of the MRZ
- Return English version of issue authority.

Rules:
- Output only a valid JSON object, matching the structure below.
- Do NOT include any explanations, comments, or additional formatting.
- All text must be translated into Russian, except numbers, document codes, and dates and issueAuthority.
- If a field is missing or not found, set its value to null (not "...").

JSON output format:
{
  "isPassport": true,
  "country_code": "KGZ",
  "number": "PE1234567",
  "issueDate": "01.01.2020",
  "expiryDate": "01.01.2030",
  "issueAuthority": "MIA 213031",
  "person": {
    "surname": "СЕРГЕЕВ",
    "surname_in_eng": "SERGEEV",
    "name": "ВЛАДИСЛАВ",
    "name_in_eng": "VLADISLAV",
    "patronymic": "АЛЕКСАНДРОВИЧ",
    "patronymic_in_eng": "ALEKSANDROVICH",
    "birth_date": "31.06.2003",
    "gender": "М",
    "birth_place": "КЫРГЫЗСКАЯ РЕСПУБЛИКА",
    "personal_number": "21004500200010"
  }
}
""";

    private static final String AZE_PASSPORT = """
You are an expert in document analysis and sworn translation from Azerbaijani into Russian.

Task:
- Extract all relevant data from the provided passport image or scan.
- Translate all necessary fields from Azerbaijani and English language into proper formal Russian, suitable for sworn translation.
- Convert all dates to format: DD.MM.YYYY.
- Gender will be Ж or М.
- Translate Azerbaijani letters into Russian properly.
- Return birthplace without its country code.

Rules:
- Output only a valid JSON object, matching the structure below.
- Do NOT include any explanations, comments, or additional formatting.
- All text must be translated into Russian, except numbers, document codes, and dates.
- If a field is missing or not found, set its value to null (not "...").

JSON output format:
{
  "isPassport": true,
  "country_code": "AZE",
  "number": "C1234567",
  "issueDate": "01.01.2020",
  "expiryDate": "01.01.2030",
  "issueAuthority": "МИНИСТЕРСТВО ВНУТРЕННИХ ДЕЛ",
  "person": {
    "surname": "АСАДОВ",
    "name": "ИЛХАМ",
    "patronymic": "МУХСИН ОГЛУ",
    "birth_date": "31.06.2003",
    "gender": "М",
    "birth_place": "АЗЕРБАЙДЖАН",
    "personal_number": "0X1232X"
  }
}
""";
    private static final String TJK_PASSPORT = """
You are an expert in document analysis and sworn translation from Tajik and English into Russian.

Task:
- Extract all relevant data from the provided passport image or scan.
- Translate all necessary fields from Tajik and English into proper formal Russian, suitable for sworn translation.
- Ҷ letter will be ДЖ.
- Convert all dates to format: DD.MM.YYYY.
- Gender will be Ж or М.
- issue authority from Tajik to Russian: ШВКД -> ОМВД; ХШБ -> ПРС; ВКД -> МВД; ШММ -> КУС;

Rules:
- Output only a valid JSON object, matching the structure below.
- Do NOT include any explanations, comments, or additional formatting.
- All text must be translated into Russian, except numbers, document codes, and dates.
- If a field is missing or not found, set its value to null (not "...").

JSON output format:
{
  "isPassport": true,
  "country_code": "TJK",
  "number": "401234567",
  "issueDate": "01.01.2020",
  "expiryDate": "01.01.2030",
  "issueAuthority": "ОМВД по г. Бустон",
  "person": {
    "surname": "ОДИЛОВ",
    "name": "ДЖАВОХИР",
    "patronymic": "ОДИЛДЖОНОВИЧ",
    "birth_date": "31.06.2003",
    "gender": "М"
  }
}
""";
    private static final String TKM_PASSPORT = """
You are an expert in document analysis and sworn translation from Turkmen into Russian.

Task:
- Extract all relevant data from the provided passport image or scan.
- Translate all necessary fields from Turkmen and English language into proper formal Russian, suitable for sworn translation.
- Convert all dates to format: DD.MM.YYYY.
-Translate Turkmen letters into Russian properly.
- Gender will be Ж or М.
- Convert country code in birth_place into proper Russian, like TKM -> ТУРКМЕНИСТАН
- Return English version of issue authority.

Rules:
- Output only a valid JSON object, matching the structure below.
- Do NOT include any explanations, comments, or additional formatting.
- All text must be translated into Russian, except numbers, document codes, and dates.
- If a field is missing or not found, set its value to null (not "...").

JSON output format:
{
  "isPassport": true,
  "country_code": "TKM",
  "number": "A1234567",
  "issueDate": "01.01.2020",
  "expiryDate": "01.01.2030",
  "issueAuthority": "SMST",
  "person": {
    "surname": "ГЕЛЬДИЕВ",
    "name": "ХЕМРА",
    "patronymic": "АЛЕКСАНДРОВИЧ",
    "birth_date": "31.06.2003",
    "gender": "М",
    "birth_place": "ТУРКМЕНИСТАН",
    "personal_number": "DB50020010"
  }
}
""";
    private static final String IND_PASSPORT = """
You are an expert in document analysis and sworn translation.

Task:
- Extract all relevant data from the provided passport image or scan.
- Translate all necessary fields from English into proper formal Russian, suitable for sworn translation.
- Convert all dates to format: DD.MM.YYYY.
- Gender will be Ж or М.
- Do not leave any field without translation.

Rules:
- Output only a valid JSON object, matching the structure below.
- Do NOT include any explanations, comments, or additional formatting.
- All text must be translated into Russian, except numbers, document codes, and dates.
- If a field is missing or not found, set its value to null (not "...").

JSON output format:
{
  "isPassport": true,
  "country_code": "IND",
  "number": "W1234567",
  "issueDate": "01.01.2020",
  "expiryDate": "01.01.2030",
  "place_of_issue": "ВИСАКХАПАТНАМ",
  "person": {
    "surname": "ГУДИЯ",
    "name": "САНКАРУ",
    "birth_date": "31.06.2003",
    "gender": "М",
    "birth_place": "КОТТА КОДЖИРИЯ, АНДХРА-ПРАДЕШ",
  }
}
""";
    private static final String TUR_PASSPORT = """
You are an expert in document analysis and sworn translation from Turkish into Russian.

Task:
- Extract all relevant data from the provided passport image or scan.
- Translate all necessary fields from Turkish into proper formal Russian, suitable for sworn translation.
- Convert all dates to format: DD.MM.YYYY.
- Gender will be Ж or М.
- Translate turkish letters into russian properly.
- Translate issued_by field properly.

Rules:
- Output only a valid JSON object, matching the structure below.
- Do NOT include any explanations, comments, or additional formatting.
- All text must be translated into Russian, except numbers, document codes, and dates.
- If a field is missing or not found, set its value to null (not "...").
- If the personal number (11 digits) is not readable elsewhere, extract it from the second MRZ line, characters 29–39.

JSON output format:
{
  "isPassport": true,
  "country_code": "TUR",
  "number": "U1234567",
  "issueDate": "01.01.2020",
  "expiryDate": "01.01.2030",
  "issued_by": "АНКАРА",
  "person": {
    "surname": "САДЫК",
    "name": "ЙЫЛМАЗ",
    "birth_date": "31.06.2003",
    "gender": "М",
    "birth_place": "ДОГАНХИСАР",
    "personal_number": "12345678901"
  }
}
""";



    public static String getRequestText(DocumentType mode, CountryCode countryCode,  Long chatId) {
        if (countryCode == null) {
            return undetectedCountry;
        }
        if (Objects.equals(chatId, SpecialUserDetails.ASHIM_CHAT_ID) && countryCode == CountryCode.KGZ)
            return KGZ_PASSPORT_ASHIM;
        if(mode == DocumentType.DRIVER_LICENSE) {
            throw new BotException("этот сервис еще не готов)");
        }
        String ret;
        switch (countryCode) {
            case AZE -> ret = AZE_PASSPORT;
            case TJK -> ret = TJK_PASSPORT;
            case IND -> ret = IND_PASSPORT;
            case TUR -> ret = TUR_PASSPORT;
            case TKM -> ret = TKM_PASSPORT;
            case PHL -> ret = PHL_PASSPORT;
            default -> ret = undetectedCountry;
        }
        return ret;
    }
}
