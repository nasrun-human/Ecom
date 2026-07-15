package com.example.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ตาราง products — ข้อมูลสินค้า
 */
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_products_sku", columnList = "sku", unique = true),
        @Index(name = "idx_products_category", columnList = "category_id")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String sku;  // Stock Keeping Unit

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "sale_price", precision = 12, scale = 2)
    private BigDecimal salePrice;  // ราคาลดพิเศษ

    @NotNull @Min(0)
    @Column(nullable = false)
    @Builder.Default
    private Integer stock = 0;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "weight_kg", precision = 8, scale = 3)
    private BigDecimal weightKg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
