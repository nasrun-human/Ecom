package com.example.app.controller;

import com.example.app.dto.CategoryDto;
import com.example.app.dto.ProductDto;
import com.example.app.repository.CategoryRepository;
import com.example.app.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    /** ทดสอบ API ทำงานไหม */
    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "message", "Spring Boot E-Commerce API is running! 🚀",
                "version", "1.0.0"
        ));
    }

    /** ดูสินค้าทั้งหมด (ใช้ DTO เพื่อป้องกัน LazyInitializationException) */
    @GetMapping("/products")
    public ResponseEntity<Page<ProductDto>> getProducts(Pageable pageable) {
        Page<ProductDto> page = productRepository
                .findByActiveTrue(pageable)
                .map(ProductDto::from);
        return ResponseEntity.ok(page);
    }

    /** ดูสินค้าตาม ID */
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(p -> ResponseEntity.ok(ProductDto.from(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    /** ค้นหาสินค้า */
    @GetMapping("/search")
    public ResponseEntity<Page<ProductDto>> searchProducts(
            @RequestParam String keyword, Pageable pageable) {
        Page<ProductDto> page = productRepository
                .searchByKeyword(keyword, pageable)
                .map(ProductDto::from);
        return ResponseEntity.ok(page);
    }

    /** ดูหมวดหมู่ทั้งหมด */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getCategories() {
        List<CategoryDto> list = categoryRepository
                .findByActiveTrue()
                .stream()
                .map(CategoryDto::from)
                .toList();
        return ResponseEntity.ok(list);
    }

    /** ดูสินค้าตามหมวดหมู่ */
    @GetMapping("/categories/{id}/products")
    public ResponseEntity<Page<ProductDto>> getProductsByCategory(
            @PathVariable Long id, Pageable pageable) {
        Page<ProductDto> page = productRepository
                .findByCategoryIdAndActiveTrue(id, pageable)
                .map(ProductDto::from);
        return ResponseEntity.ok(page);
    }

    /** สถิติ */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(Map.of(
                "totalProducts", productRepository.count(),
                "totalCategories", categoryRepository.count()
        ));
    }
}
