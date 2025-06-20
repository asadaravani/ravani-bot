package com.ravani.ravanibot.service.impl;

import com.ravani.ravanibot.dtos.Passport;
import com.ravani.ravanibot.enums.Countries;
import com.ravani.ravanibot.exceptions.FileDownloadingErrorException;
import com.ravani.ravanibot.exceptions.UnsupportedDocumentException;
import com.ravani.ravanibot.service.DocumentService;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
public class DocumentServiceIMpl implements DocumentService {

    @Override
    public XWPFDocument fillWordDocument(Long chatId, Passport passport) {
        XWPFDocument document;
        Map<String, String> fields;
        Countries country = detectCountry(chatId, passport.country().toUpperCase());
        switch (country) {
            case UZB -> {
                document = loadFile(chatId, "docs/uzb_passport.docx");
                fields = DocumentServiceUtil.mapFieldsUzb(passport);
                replaceFieldInLayer(document, fields);
                return document;
            }
            case ARM -> {
                document = loadFile(chatId, "docs/arm_passport.docx");
                fields = DocumentServiceUtil.mapFieldsArm(passport);
                replaceFieldInLayer(document, fields);
                return document;
            }
            case KGZ -> {
                if (passport.number().toUpperCase().contains("AC")) {
                    document = loadFile(chatId, "docs/kgz_passport_old.docx");
                    fields = DocumentServiceUtil.mapFieldsKgzOld(passport);
                    break;
                }
                    document = loadFile(chatId, "docs/kgz_passport_new.docx");
                    fields = DocumentServiceUtil.mapFieldsKgzNew(passport);
            }
            case TJK -> {
                document = loadFile(chatId, "docs/tjk_passport.docx");
                fields = DocumentServiceUtil.mapFieldsTjk(passport);
            }
            case AZE ->  {
                document = loadFile(chatId, "docs/aze_passport.docx");
                fields = DocumentServiceUtil.mapFieldsAze(passport);
            }
            default -> throw new UnsupportedDocumentException(chatId, "Definitely unexpected exception in the DocService:switch. Contact _admin_");
        }

        replaceField(document.getParagraphs(), fields);
        return document;
    }

    private XWPFDocument loadFile(Long chatId, String filePath) {
        try (InputStream templateStream = getClass().getClassLoader().getResourceAsStream(filePath)){
            if (templateStream == null)
                throw new FileDownloadingErrorException(chatId, "❌File from 📁resources is null" );
            return new XWPFDocument(templateStream);
        }catch (Exception e){
            throw new FileDownloadingErrorException(chatId, "❌Cannot load file from 📁resources: " + filePath);
        }
    }
    private void replaceField(List<XWPFParagraph> paragraphs, Map<String, String> values) {
        paragraphs.forEach(paragraph ->
                paragraph.getRuns().forEach(run -> {
                    String text = run.getText(0);
                    if (text != null) {
                        for (Map.Entry<String, String> entry : values.entrySet()) {
                            if (text.contains(entry.getKey())) {
                                run.setText(text.replace(entry.getKey(), entry.getValue() != null ? entry.getValue() : ""), 0);
                            }
                        }
                    }
                })
        );
    }
    private void replaceFieldInLayer(XWPFDocument doc, Map<String, String> values) {
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
