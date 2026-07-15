package com.example.app.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StockTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "stock_before", nullable = false)
    private Integer stockBefore;

    @Column(name = "stock_after", nullable = false)
    private Integer stockAfter;

    @Column(length = 500)
    private String reason;

    @Column(name = "reference_id")
    private Long referenceId; // orderId ถ้าเป็นการขาย

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum TransactionType {
        IN,     // รับสินค้าเข้า
        OUT,    // ตัดสต๊อก (ขาย)
        ADJUST, // ปรับสต๊อก
        RETURN  // คืนสินค้า
    }
}
