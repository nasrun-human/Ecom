package com.example.app.controller;

import com.example.app.entity.Order;
import com.example.app.repository.OrderRepository;
import com.example.app.service.OrderService;
import com.example.app.service.PromptPayService;
import com.example.app.service.QrCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PromptPayService promptPayService;
    private final QrCodeService qrCodeService;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    // PromptPay phone number ของร้าน (config ที่นี่)
    private static final String SHOP_PHONE = "0812345678";

    /**
     * GET /api/payment/qr/{orderId}
     * สร้าง PromptPay QR Code สำหรับ order นั้น
     */
    @GetMapping("/qr/{orderId}")
    public ResponseEntity<Map<String, Object>> getPaymentQr(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ไม่พบ Order ID: " + orderId));

        if (order.getPaymentStatus() == Order.PaymentStatus.PAID) {
            return ResponseEntity.ok(Map.of(
                "paid", true,
                "orderNumber", order.getOrderNumber(),
                "status", order.getStatus().name()
            ));
        }

        String payload = promptPayService.generatePhoneQR(SHOP_PHONE, order.getTotalAmount());
        String qrBase64 = qrCodeService.generateBase64QR(payload, 400);

        return ResponseEntity.ok(Map.of(
            "paid", false,
            "orderId", order.getId(),
            "orderNumber", order.getOrderNumber(),
            "totalAmount", order.getTotalAmount(),
            "qrBase64", qrBase64,
            "promptPayPhone", SHOP_PHONE,
            "payload", payload
        ));
    }

    /**
     * GET /api/payment/status/{orderId}
     * ตรวจสอบสถานะการชำระ (Frontend poll ทุก 5 วินาที)
     */
    @GetMapping("/status/{orderId}")
    public ResponseEntity<Map<String, Object>> checkStatus(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ไม่พบ Order"));

        return ResponseEntity.ok(Map.of(
            "orderId", order.getId(),
            "orderNumber", order.getOrderNumber(),
            "paymentStatus", order.getPaymentStatus().name(),
            "orderStatus", order.getStatus().name(),
            "paid", order.getPaymentStatus() == Order.PaymentStatus.PAID,
            "totalAmount", order.getTotalAmount()
        ));
    }

    /**
     * POST /api/payment/confirm/{orderId} (Admin only)
     * Admin ยืนยันการชำระเงิน
     */
    @PostMapping("/confirm/{orderId}")
    public ResponseEntity<Map<String, Object>> confirmPayment(
            @PathVariable Long orderId,
            org.springframework.security.core.Authentication auth) {
        String confirmedBy = auth != null ? auth.getName() : "admin";
        Order order = orderService.confirmPayment(orderId, confirmedBy);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "\u0e22\u0e37\u0e19\u0e22\u0e31\u0e19\u0e01\u0e32\u0e23\u0e0a\u0e33\u0e23\u0e30\u0e40\u0e07\u0e34\u0e19 Order " + order.getOrderNumber() + " \u0e2a\u0e33\u0e40\u0e23\u0e47\u0e08",
            "orderNumber", order.getOrderNumber(),
            "status", order.getStatus().name()
        ));
    }
}
