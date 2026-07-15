package com.example.app.dto;

import com.example.app.entity.Category;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO สำหรับ Category — ป้องกัน circular reference และ LazyInitializationException
 */
@Data
public class CategoryDto {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Boolean active;
    private Long parentId;
    private String parentName;
    private LocalDateTime createdAt;

    public static CategoryDto from(Category c) {
        CategoryDto dto = new CategoryDto();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setDescription(c.getDescription());
        dto.setImageUrl(c.getImageUrl());
        dto.setActive(c.getActive());
        dto.setCreatedAt(c.getCreatedAt());
        if (c.getParent() != null) {
            dto.setParentId(c.getParent().getId());
            dto.setParentName(c.getParent().getName());
        }
        return dto;
    }
}
