package com.ravani.ravanibot.service.impl;

import com.ravani.ravanibot.dtos.Passport;
import com.ravani.ravanibot.exceptions.FileDownloadingErrorException;
import com.ravani.ravanibot.exceptions.UnsupportedDocumentException;
import com.ravani.ravanibot.service.DocumentService;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DocumentServiceIMpl implements DocumentService {

    @Override
    public XWPFDocument fillWordDocument(Long chatId, Passport passport) {
        XWPFDocument document;
        boolean kyrgyz = passport.country().toUpperCase().contains("КЫРГЫЗ");

        if (kyrgyz && passport.number().toUpperCase().contains("PE"))
            document = loadFile(chatId, "docs/kgz_passport_new.docx");
        else if (kyrgyz && passport.number().toUpperCase().contains("AC"))
            document = loadFile(chatId, "docs/kgz_passport_old.docx");
        else if (passport.country().toUpperCase().contains("УЗБЕК")) {
            document = loadFile(chatId, "docs/uzb_passport.docx");
            Map<String, String> fields = mapFields(passport);
            replaceFieldUzb(document, fields);
            return document;
        }
        else if (passport.country().toUpperCase().contains("ТАДЖИК"))
            document = loadFile(chatId, "docs/tjk_passport.docx");
        else throw new UnsupportedDocumentException(chatId, "❌Паспорт не поддерживается. Принимаются только паспорта КР, РУз или РТ.");

        Map<String, String> fields = mapFields(passport);
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
    private Map<String, String> mapFields(Passport passport) {
        String patronymic = passport.person().patronymic() == null ? "" : passport.person().patronymic();
        String birth_place = passport.person().birth_place() ==  null ? "" : passport.person().birth_place().toUpperCase();

        Map<String, String> values = new HashMap<>();
        values.put("Поля0", passport.number());
        values.put("Поля1", passport.person().surname().toUpperCase());
        values.put("Поля2", passport.person().name().toUpperCase());
        values.put("Поля3", patronymic.toUpperCase());
        values.put("Поля4", passport.person().birth_date());
        values.put("Поля5", passport.person().personal_number());
        values.put("Поля6", birth_place);
        values.put("Поля7", passport.issueDate());
        values.put("Поля8", passport.expiryDate());
        values.put("Поля9", translateAuthority(passport.issueAuthority().toUpperCase()));
        values.put("Ген1", passport.person().gender().toUpperCase());
        return values;
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
    private void replaceFieldUzb(XWPFDocument doc, Map<String, String> values) {
        doc.getTables().forEach(table -> {
            table.getRows().forEach(row -> {
                row.getTableCells().forEach(tableCell -> {
                    replaceField(tableCell.getParagraphs(), values);
                });
            });
        });
    }
    private String translateAuthority(String authority) {
        if(authority.contains("SRS"))
            authority = authority.replace("SRS", "ГРС");
        if(authority.contains("MIA"))
            authority = authority.replace("MIA", "МВД");
        return authority;
    }
}
