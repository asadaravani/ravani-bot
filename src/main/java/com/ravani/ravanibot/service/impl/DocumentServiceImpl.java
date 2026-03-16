package com.ravani.ravanibot.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.ravani.ravanibot.dtos.DocumentDto;
import com.ravani.ravanibot.dtos.DriverLicenseDto;
import com.ravani.ravanibot.dtos.PassportDto;
import com.ravani.ravanibot.enums.CountryCode;
import com.ravani.ravanibot.enums.DocumentType;
import com.ravani.ravanibot.exceptions.BotException;
import com.ravani.ravanibot.service.DocumentService;
import org.apache.poi.xwpf.usermodel.*;
import org.jsonrepairj.JsonRepair;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.jsonrepairj.Json.MAPPER;

@Component
public class DocumentServiceImpl implements DocumentService {

    @Override
    public XWPFDocument fillWordDocument(Long chatId, DocumentDto dto) {
        CountryCode country = detectCountry(dto.getCountry_code());
        return dto instanceof PassportDto ? PassportDocGenerator.execute(country, (PassportDto) dto, chatId)
                : DriverLicenseDocGenerator.execute(country, (DriverLicenseDto) dto, chatId);
    }

    @Override
    public DocumentDto mapToDocumentDto(String response, DocumentType type) {
        try {
            JsonNode node;

            try {
                node = MAPPER.readTree(response);
            } catch (Exception e) {
                response = JsonRepair.repairJson(response);
                node = MAPPER.readTree(response);
            }

            if (type == DocumentType.PASSPORT) {

                if (node.isArray()) {
                    List<PassportDto> documents =
                            MAPPER.convertValue(node, new TypeReference<List<PassportDto>>() {});

                    return documents.stream()
                            .filter(DocumentDto::isDocument)
                            .findFirst()
                            .orElse(documents.get(0));
                }

                return MAPPER.treeToValue(node, PassportDto.class);
            }

            return MAPPER.treeToValue(node, DriverLicenseDto.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse document JSON", e);
        }
    }

    static XWPFDocument loadFile(String filePath) {
        try (InputStream templateStream = DocumentServiceImpl.class.getClassLoader().getResourceAsStream(filePath)){
            if (templateStream == null)
                throw new BotException( "❌File from 📁resources is null" );
            return new XWPFDocument(templateStream);
        }catch (Exception e){
            throw new BotException("❌Cannot load file from 📁resources");
        }
    }
    static void replaceField(List<XWPFParagraph> paragraphs, Map<String, String> values) {
        paragraphs.forEach(paragraph ->
                paragraph.getRuns().forEach(run -> {
                    String text = run.getText(0);
                    if (text != null) {
                        for (Map.Entry<String, String> entry : values.entrySet()) {
                            if (text.contains(entry.getKey())) {
                                run.setText(text.replace(entry.getKey(), entry.getValue()), 0);
                            }
                        }
                    }
                })
        );
    }
    static void replaceFieldInLayer(XWPFDocument doc, Map<String, String> values) {
        doc.getTables().forEach(table -> {
            replaceFieldInTables(table, values);
        });
    }
    static void replaceFieldInTables(XWPFTable table, Map<String, String> values) {
        table.getRows().forEach(row -> {
            row.getTableCells().forEach(tableCell -> {
                replaceField(tableCell.getParagraphs(), values);

                tableCell.getTables().forEach(nestedTable -> {
                    replaceFieldInTables(nestedTable, values);
                });
            });
        });
    }
    private CountryCode detectCountry(String country_code) {
        CountryCode country;
        switch (country_code) {
            case "KGZ" -> country = CountryCode.KGZ;
            case "UZB" -> country = CountryCode.UZB;
            case "TJK" -> country = CountryCode.TJK;
            case "KAZ" -> country = CountryCode.KAZ;
            case "TKM" -> country = CountryCode.TKM;
            case "AZE" -> country = CountryCode.AZE;
            case "ARM" -> country = CountryCode.ARM;
            case "TUR" -> country = CountryCode.TUR;
            case "IND" -> country = CountryCode.IND;
            case "PHL" -> country = CountryCode.PHL;
            default ->
                    throw new BotException("❌Паспорт " + country_code + " не поддерживается");
        }
        return country;
    }
}
