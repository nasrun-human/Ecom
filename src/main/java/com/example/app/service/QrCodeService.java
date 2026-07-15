package com.example.app.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;
import javax.imageio.ImageIO;

@Service
public class QrCodeService {

    /**
     * แปลง payload string → Base64 PNG image
     */
    public String generateBase64QR(String payload, int size) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = Map.of(
                EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M,
                EncodeHintType.CHARACTER_SET, "UTF-8",
                EncodeHintType.MARGIN, 1
            );
            BitMatrix matrix = writer.encode(payload, BarcodeFormat.QR_CODE, size, size, hints);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", out);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (WriterException | java.io.IOException e) {
            throw new RuntimeException("ไม่สามารถสร้าง QR Code ได้: " + e.getMessage(), e);
        }
    }
}
