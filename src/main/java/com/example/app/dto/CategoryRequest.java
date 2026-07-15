package com.example.app.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Request DTO สำหรับสร้าง/แก้ไขหมวดหมู่
 */
@Data
public class CategoryRequest {

    @NotBlank(message = "ชื่อหมวดหมู่ห้ามว่าง")
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    private String imageUrl;

    private Long parentId;  // null = หมวดหมู่หลัก

    private Boolean active = true;
}
