package com.example.app.service;

import com.example.app.entity.Product;
import com.example.app.entity.StockTransaction;
import com.example.app.repository.ProductRepository;
import com.example.app.repository.StockTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final ProductRepository productRepository;
    private final StockTransactionRepository stockTransactionRepository;

    /** เพิ่มสต๊อก */
    @Transactional
    public Map<String, Object> addStock(Long productId, int quantity, String reason, String by) {
        Product p = getProduct(productId);
        int before = p.getStock();
        p.setStock(before + quantity);
        productRepository.save(p);
        log(p, StockTransaction.TransactionType.IN, quantity, before, p.getStock(), reason, null, by);
        log.info("✅ เพิ่มสต๊อก {} +{} → {}", p.getName(), quantity, p.getStock());
        return result(p, before, quantity, "เพิ่มสต๊อกสำเร็จ");
    }

    /** ตัดสต๊อก */
    @Transactional
    public Map<String, Object> deductStock(Long productId, int quantity, String reason, Long orderId, String by) {
        Product p = getProduct(productId);
        if (p.getStock() < quantity)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "สต๊อกไม่พอ: มี " + p.getStock() + " ต้องการ " + quantity);
        int before = p.getStock();
        p.setStock(before - quantity);
        productRepository.save(p);
        log(p, StockTransaction.TransactionType.OUT, quantity, before, p.getStock(), reason, orderId, by);
        log.info("📦 ตัดสต๊อก {} -{} → {}", p.getName(), quantity, p.getStock());
        return result(p, before, -quantity, "ตัดสต๊อกสำเร็จ");
    }

    /** ดูประวัติ stock */
    public List<StockTransaction> getHistory(Long productId) {
        return stockTransactionRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    /** ล่าสุด 10 รายการ (ทุกสินค้า) */
    public List<StockTransaction> getRecentTransactions() {
        return stockTransactionRepository.findTop10ByOrderByCreatedAtDesc();
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ไม่พบสินค้า ID: " + id));
    }

    private void log(Product p, StockTransaction.TransactionType type, int qty,
                     int before, int after, String reason, Long refId, String by) {
        stockTransactionRepository.save(StockTransaction.builder()
            .product(p).type(type).quantity(qty)
            .stockBefore(before).stockAfter(after)
            .reason(reason).referenceId(refId).createdBy(by)
            .build());
    }

    private Map<String, Object> result(Product p, int before, int delta, String msg) {
        return Map.of(
            "productId", p.getId(), "productName", p.getName(),
            "stockBefore", before, "stockAfter", p.getStock(),
            "delta", delta, "message", msg
        );
    }
}
