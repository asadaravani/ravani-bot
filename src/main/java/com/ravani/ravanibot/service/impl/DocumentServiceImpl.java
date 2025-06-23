package com.ravani.ravanibot.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ravani.ravanibot.dtos.DocumentDto;
import com.ravani.ravanibot.dtos.DriverLicenseDto;
import com.ravani.ravanibot.dtos.PassportDto;
import com.ravani.ravanibot.enums.Countries;
import com.ravani.ravanibot.enums.DocumentType;
import com.ravani.ravanibot.exceptions.FileDownloadingErrorException;
import com.ravani.ravanibot.exceptions.UnsupportedDocumentException;
import com.ravani.ravanibot.service.DocumentService;
import lombok.SneakyThrows;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
public class DocumentServiceImpl implements DocumentService {

    @Override
    public XWPFDocument fillWordDocument(Long chatId, DocumentDto dto) {
        Countries country = detectCountry(chatId, dto.getCountry());
        return dto instanceof PassportDto ? PassportDocGenerator.execute(country, (PassportDto) dto, chatId)
                : DriverLicenseDocGenerator.execute(country, (DriverLicenseDto) dto, chatId);
    }

    @SneakyThrows
    @Override
    public DocumentDto mapToDocumentDto(String response, DocumentType type) {
        ObjectMapper mapper = new ObjectMapper();
        if (type == DocumentType.PASSPORT)
            return mapper.readValue(response, PassportDto.class);
        return mapper.readValue(response, DriverLicenseDto.class);
    }

    static XWPFDocument loadFile(Long chatId, String filePath) {
        try (InputStream templateStream = DocumentServiceImpl.class.getClassLoader().getResourceAsStream(filePath)){
            if (templateStream == null)
                throw new FileDownloadingErrorException(chatId, "❌File from 📁resources is null" );
            return new XWPFDocument(templateStream);
        }catch (Exception e){
            throw new FileDownloadingErrorException(chatId, "❌Cannot load file from 📁resources: " + filePath);
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
            table.getRows().forEach(row -> {
                row.getTableCells().forEach(tableCell -> {
                    replaceField(tableCell.getParagraphs(), values);
                });
            });
        });
    }
    private Countries detectCountry(Long chatId, String country) {
        return Map.of(
                Countries.KGZ, "КЫРГЫЗ",
                Countries.UZB, "УЗБЕК",
                Countries.TJK, "ТАДЖИК",
                Countries.AZE, "АЗЕР",
                Countries.ARM, "АРМЕН"
        ).entrySet().stream()
                .filter(entry -> country.contains(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new UnsupportedDocumentException(chatId, "❌Паспорт не поддерживается. Принимаются только паспорта KGZ, UZB, TJK, AZE, ARM."));
    }
}
