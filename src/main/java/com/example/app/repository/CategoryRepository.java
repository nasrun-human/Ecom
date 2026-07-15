package com.example.app.repository;

import com.example.app.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNull();           // หมวดหมู่หลัก
    List<Category> findByParentId(Long parentId);  // หมวดหมู่ย่อย
    List<Category> findByActiveTrue();
}
