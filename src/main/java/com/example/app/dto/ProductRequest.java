package com.example.app.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Request DTO สำหรับสร้าง/แก้ไขสินค้า
 */
@Data
public class ProductRequest {

    @NotBlank(message = "ชื่อสินค้าห้ามว่าง")
    @Size(max = 255)
    private String name;

    private String description;

    @NotBlank(message = "SKU ห้ามว่าง")
    @Size(max = 100)
    private String sku;

    @NotNull(message = "ราคาห้ามว่าง")
    @DecimalMin(value = "0.0", message = "ราคาต้องมากกว่า 0")
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "ราคา sale ต้องมากกว่า 0")
    private BigDecimal salePrice;

    @NotNull(message = "จำนวนสต๊อกห้ามว่าง")
    @Min(value = 0, message = "สต๊อกต้องไม่ติดลบ")
    private Integer stock;

    private String imageUrl;

    private BigDecimal weightKg;

    @NotNull(message = "กรุณาเลือกหมวดหมู่")
    private Long categoryId;

    private Boolean active = true;
}
