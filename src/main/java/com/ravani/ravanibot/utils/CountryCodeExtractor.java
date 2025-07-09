package com.ravani.ravanibot.utils;

import com.ravani.ravanibot.config.TesseractConfig;
import com.ravani.ravanibot.dtos.DownloadedFile;
import com.ravani.ravanibot.enums.CountryCode;
import com.ravani.ravanibot.exceptions.AdminPanelException;
import com.ravani.ravanibot.exceptions.FileDownloadingErrorException;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CountryCodeExtractor {
    private final Tesseract tesseract;

    private static final Map<String, CountryCode> COUNTRY_NAME_TO_CODE = Map.ofEntries(
            Map.entry("KYRGYZ", CountryCode.KGZ),
            Map.entry("ZBEKIS", CountryCode.UZB),
            Map.entry("TAJIK", CountryCode.TJK),
            Map.entry("ARMENIA", CountryCode.ARM),
            Map.entry("AZER", CountryCode.AZE),
            Map.entry("IND", CountryCode.IND),
            Map.entry("TURKMEN", CountryCode.TKM),
            Map.entry("TURKIYE", CountryCode.TUR),
            Map.entry("KAZAKHSTAN", CountryCode.KAZ)
    );

    public CountryCodeExtractor() {
        this.tesseract = new Tesseract();
        tesseract.setLanguage("eng");
        TesseractConfig tesseractConfig = new TesseractConfig();
        tesseract.setDatapath(tesseractConfig.getDataPath());
        tesseract.setVariable("user_defined_dpi", "300");
        tesseract.setVariable("tessedit_char_whitelist", "ABCDEFGHIJKLMNOPQRSTUVWXYZ<");
        tesseract.setOcrEngineMode(1);
        tesseract.setPageSegMode(6);
    }
    public CountryCode extract(Long chatId, DownloadedFile file) {
        try {
            if (file.contentType().equals("application/pdf")) {
                return extractFromPDF(file);
            }
            return extractFromImg(file);
        }catch (OutOfMemoryError e){
                throw new FileDownloadingErrorException(chatId, "Файл слишком большой, вы можете сделать скриншот и отпарвить снова. Главное были видны все буквы, маленькие тоже");
        }
        catch (IOException | TesseractException e) {
            throw new AdminPanelException("Failed to extract country code. ChatId: " + chatId);
        }
    }
    private CountryCode extractFromPDF(DownloadedFile file) throws IOException, TesseractException {
        try (
                RandomAccessRead rar = new RandomAccessReadBuffer(file.bytes());
                PDDocument doc = Loader.loadPDF(rar)
        ) {
            PDFRenderer renderer = new PDFRenderer(doc);
            BufferedImage image = renderer.renderImageWithDPI(0, 300);
            BufferedImage grayImage = toGrayscale(image);
            BufferedImage contrasted = enhanceContrast(grayImage);
            String text = tesseract.doOCR(contrasted).toUpperCase();
            return extractCountryCodeFromText(text);
        }
    }
    private CountryCode extractFromImg(DownloadedFile file) throws IOException, TesseractException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(file.bytes())) {
            BufferedImage image = ImageIO.read(bais);
            if (image == null) {
                throw new IOException("Image is null");
            }
            BufferedImage grayImage = toGrayscale(image);
            BufferedImage contrasted = enhanceContrast(grayImage);
            BufferedImage binarizedImage = binarize(contrasted);
            String text = tesseract.doOCR(binarizedImage).toUpperCase();
            return extractCountryCodeFromText(text);
        }
    }
    private BufferedImage toGrayscale(BufferedImage original) {
        BufferedImage grayscale = new BufferedImage(
                original.getWidth(),
                original.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY
        );
        java.awt.Graphics g = grayscale.getGraphics();
        g.drawImage(original, 0, 0, null);
        g.dispose();
        return grayscale;
    }
    private BufferedImage enhanceContrast(BufferedImage image) {
        RescaleOp op = new RescaleOp(2.0f, -50, null); // Brightness factor
        op.filter(image, image);
        return image;
    }

    private BufferedImage binarize(BufferedImage gray) {
        BufferedImage binary = new BufferedImage(gray.getWidth(), gray.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g = binary.createGraphics();
        g.drawImage(gray, 0, 0, null);
        g.dispose();
        return binary;
    }
    private CountryCode extractCountryCodeFromText(String text) {
        String[] patterns = {
                "P<([A-Z]{3})",
                "P([A-Z]{3})",
                "P.?([A-Z]{2})",
                "P.{0,3}?([A-Z]{3})"
        };

        for (String pat : patterns) {
            Matcher matcher = Pattern.compile(pat).matcher(text);
            if (matcher.find()) {
                String code = matcher.group(1);
                try {
                    return CountryCode.valueOf(code);
                } catch (IllegalArgumentException ignored) {}
            }
        }
        if (text.contains("PCAZE")) {
            return CountryCode.valueOf("AZE");
        }
        if (text.contains("P<TIK") ) {}
        for (Map.Entry<String, CountryCode> entry : COUNTRY_NAME_TO_CODE.entrySet()) {
            if (text.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

}
