package com.ravani.ravanibot.service;

import com.ravani.ravanibot.dtos.DocumentDto;
import com.ravani.ravanibot.enums.DocumentType;
import lombok.SneakyThrows;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public interface DocumentService {
    XWPFDocument fillWordDocument(Long chatId, DocumentDto dto);

    @SneakyThrows
    DocumentDto mapToDocumentDto(String response, DocumentType type);
}
