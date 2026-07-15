package com.example.app.dto;

import com.example.app.entity.Product;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO สำหรับ Product — ป้องกัน LazyInitializationException
 */
@Data
public class ProductDto {

    private Long id;
    private String name;
    private String description;
    private String sku;
    private BigDecimal price;
    private BigDecimal salePrice;
    private Integer stock;
    private String imageUrl;
    private Boolean active;
    private BigDecimal weightKg;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Category แบบ flat (ไม่ lazy)
    private Long categoryId;
    private String categoryName;

    /** แปลง Entity → DTO */
    public static ProductDto from(Product p) {
        ProductDto dto = new ProductDto();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setSku(p.getSku());
        dto.setPrice(p.getPrice());
        dto.setSalePrice(p.getSalePrice());
        dto.setStock(p.getStock());
        dto.setImageUrl(p.getImageUrl());
        dto.setActive(p.getActive());
        dto.setWeightKg(p.getWeightKg());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setUpdatedAt(p.getUpdatedAt());

        if (p.getCategory() != null) {
            dto.setCategoryId(p.getCategory().getId());
            dto.setCategoryName(p.getCategory().getName());
        }
        return dto;
    }
}
