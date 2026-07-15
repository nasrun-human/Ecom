package com.example.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ตาราง orders — คำสั่งซื้อ
 */
@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_orders_user", columnList = "user_id"),
        @Index(name = "idx_orders_status", columnList = "status")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", unique = true, nullable = false, length = 50)
    private String orderNumber;  // เช่น "ORD-20250715-0001"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @NotNull
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "discount_amount", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "shipping_fee", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @NotNull
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    // ที่อยู่จัดส่ง (snapshot ตอนสั่งซื้อ)
    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Column(columnDefinition = "TEXT")
    private String note;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum OrderStatus {
        PENDING,      // รอดำเนินการ
        CONFIRMED,    // ยืนยันแล้ว
        PROCESSING,   // กำลังเตรียม
        SHIPPED,      // จัดส่งแล้ว
        DELIVERED,    // ได้รับแล้ว
        CANCELLED,    // ยกเลิก
        REFUNDED      // คืนเงินแล้ว
    }

    public enum PaymentStatus {
        UNPAID, PAID, REFUNDED
    }

    public enum PaymentMethod {
        CASH, CREDIT_CARD, BANK_TRANSFER, PROMPTPAY, COD
    }
}
