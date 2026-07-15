package com.example.app.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;

/**
 * Generate PromptPay QR Code payload ตามมาตรฐาน EMV QCo (Bank of Thailand)
 */
@Service
public class PromptPayService {

    private static final String GUID = "A000000677010111";

    /**
     * สร้าง PromptPay QR payload สำหรับ Phone Number
     * @param phone หมายเลขโทรศัพท์ (0812345678)
     * @param amount จำนวนเงิน (null = ไม่กำหนด)
     */
    public String generatePhoneQR(String phone, BigDecimal amount) {
        String normalizedPhone = normalizePhone(phone);
        String merchantInfo = buildMerchantAccountInfo(normalizedPhone);
        return buildPayload(merchantInfo, amount);
    }

    /**
     * สร้าง PromptPay QR payload สำหรับ National ID / Tax ID
     */
    public String generateNationalIdQR(String nationalId, BigDecimal amount) {
        String merchantInfo = buildMerchantAccountInfo(nationalId);
        return buildPayload(merchantInfo, amount);
    }

    private String normalizePhone(String phone) {
        phone = phone.replaceAll("[^0-9]", "");
        if (phone.startsWith("0")) {
            phone = "0066" + phone.substring(1);
        }
        return phone;
    }

    private String buildMerchantAccountInfo(String id) {
        String guidTlv = tlv("00", GUID);
        String idTlv = tlv("01", id);
        return tlv("29", guidTlv + idTlv);
    }

    private String buildPayload(String merchantInfo, BigDecimal amount) {
        StringBuilder sb = new StringBuilder();
        sb.append(tlv("00", "01")); // Payload Format Indicator
        sb.append(tlv("01", amount != null ? "12" : "11")); // Initiation Method
        sb.append(merchantInfo); // Merchant Account Info
        sb.append(tlv("53", "764")); // Currency THB
        if (amount != null) {
            sb.append(tlv("54", String.format("%.2f", amount)));
        }
        sb.append(tlv("58", "TH")); // Country
        sb.append(tlv("59", "ShopVerse")); // Merchant Name
        sb.append(tlv("60", "Bangkok")); // City
        String withoutCrc = sb + "6304";
        String crc = crc16(withoutCrc);
        return withoutCrc + crc;
    }

    private String tlv(String tag, String value) {
        String len = String.format("%02d", value.length());
        return tag + len + value;
    }

    /** CRC16/CCITT-FALSE */
    private String crc16(String str) {
        int crc = 0xFFFF;
        for (byte b : str.getBytes(java.nio.charset.StandardCharsets.UTF_8)) {
            crc ^= (b & 0xFF) << 8;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0) crc = (crc << 1) ^ 0x1021;
                else crc <<= 1;
            }
        }
        return String.format("%04X", crc & 0xFFFF);
    }
}
