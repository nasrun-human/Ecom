package com.example.app.controller;

import com.example.app.entity.StockTransaction;
import com.example.app.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    /** POST /api/admin/stock/{productId}/add */
    @PostMapping("/{productId}/add")
    public ResponseEntity<Map<String, Object>> addStock(
            @PathVariable Long productId,
            @RequestBody Map<String, Object> body,
            Authentication auth) {
        int qty = Integer.parseInt(body.get("quantity").toString());
        String reason = (String) body.getOrDefault("reason", "รับสินค้าเข้า");
        String by = auth != null ? auth.getName() : "admin";
        return ResponseEntity.ok(stockService.addStock(productId, qty, reason, by));
    }

    /** POST /api/admin/stock/{productId}/deduct */
    @PostMapping("/{productId}/deduct")
    public ResponseEntity<Map<String, Object>> deductStock(
            @PathVariable Long productId,
            @RequestBody Map<String, Object> body,
            Authentication auth) {
        int qty = Integer.parseInt(body.get("quantity").toString());
        String reason = (String) body.getOrDefault("reason", "ปรับลดสต๊อก");
        String by = auth != null ? auth.getName() : "admin";
        return ResponseEntity.ok(stockService.deductStock(productId, qty, reason, null, by));
    }

    /** GET /api/admin/stock/{productId}/history */
    @GetMapping("/{productId}/history")
    public ResponseEntity<List<StockTransaction>> getHistory(@PathVariable Long productId) {
        return ResponseEntity.ok(stockService.getHistory(productId));
    }

    /** GET /api/admin/stock/recent */
    @GetMapping("/recent")
    public ResponseEntity<List<StockTransaction>> getRecent() {
        return ResponseEntity.ok(stockService.getRecentTransactions());
    }
}
