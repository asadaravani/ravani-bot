package com.ravani.ravanibot.service;

import com.ravani.ravanibot.dtos.Passport;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public interface DocumentService {
    XWPFDocument fillWordDocument(Long chatId, Passport passport);
}
