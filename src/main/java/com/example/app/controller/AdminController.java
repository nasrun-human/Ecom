package com.example.app.controller;

import com.example.app.dto.CategoryDto;
import com.example.app.dto.CategoryRequest;
import com.example.app.dto.ProductDto;
import com.example.app.dto.ProductRequest;
import com.example.app.entity.Category;
import com.example.app.entity.Product;
import com.example.app.repository.CategoryRepository;
import com.example.app.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * Admin API — ต้องล็อกอินก่อน (Basic Auth: admin / admin123)
 * Base URL: /api/admin
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // ==================== PRODUCTS ====================

    /** GET /api/admin/products — ดูสินค้าทั้งหมด (รวม inactive) */
    @GetMapping("/products")
    public ResponseEntity<Page<ProductDto>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(
            productRepository.findAll(pageable).map(ProductDto::from)
        );
    }

    /** POST /api/admin/products — เพิ่มสินค้าใหม่ */
    @PostMapping("/products")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductRequest req) {
        Category category = categoryRepository.findById(req.getCategoryId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "ไม่พบหมวดหมู่ ID: " + req.getCategoryId()));

        Product product = Product.builder()
            .name(req.getName())
            .description(req.getDescription())
            .sku(req.getSku())
            .price(req.getPrice())
            .salePrice(req.getSalePrice())
            .stock(req.getStock())
            .imageUrl(req.getImageUrl())
            .weightKg(req.getWeightKg())
            .category(category)
            .active(req.getActive() != null ? req.getActive() : true)
            .build();

        Product saved = productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductDto.from(saved));
    }

    /** PUT /api/admin/products/{id} — แก้ไขสินค้า */
    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id, @Valid @RequestBody ProductRequest req) {

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "ไม่พบสินค้า ID: " + id));

        Category category = categoryRepository.findById(req.getCategoryId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "ไม่พบหมวดหมู่ ID: " + req.getCategoryId()));

        product.setName(req.getName());
        product.setDescription(req.getDescription());
        product.setSku(req.getSku());
        product.setPrice(req.getPrice());
        product.setSalePrice(req.getSalePrice());
        product.setStock(req.getStock());
        product.setImageUrl(req.getImageUrl());
        product.setWeightKg(req.getWeightKg());
        product.setCategory(category);
        if (req.getActive() != null) product.setActive(req.getActive());

        return ResponseEntity.ok(ProductDto.from(productRepository.save(product)));
    }

    /** PATCH /api/admin/products/{id}/stock — อัปเดตสต๊อก */
    @PatchMapping("/products/{id}/stock")
    public ResponseEntity<Map<String, Object>> updateStock(
            @PathVariable Long id, @RequestBody Map<String, Integer> body) {

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "ไม่พบสินค้า ID: " + id));

        int newStock = body.getOrDefault("stock", product.getStock());
        product.setStock(newStock);
        productRepository.save(product);

        return ResponseEntity.ok(Map.of(
            "id", id,
            "name", product.getName(),
            "stock", newStock,
            "message", "อัปเดตสต๊อกสำเร็จ"
        ));
    }

    /** DELETE /api/admin/products/{id} — ลบสินค้า */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ไม่พบสินค้า ID: " + id);
        }
        productRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "ลบสินค้า ID " + id + " สำเร็จ"));
    }

    // ==================== CATEGORIES ====================

    /** GET /api/admin/categories — ดูหมวดหมู่ทั้งหมด */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(
            categoryRepository.findAll().stream().map(CategoryDto::from).toList()
        );
    }

    /** POST /api/admin/categories — เพิ่มหมวดหมู่ใหม่ */
    @PostMapping("/categories")
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryRequest req) {
        Category category = Category.builder()
            .name(req.getName())
            .description(req.getDescription())
            .imageUrl(req.getImageUrl())
            .active(req.getActive() != null ? req.getActive() : true)
            .build();

        if (req.getParentId() != null) {
            Category parent = categoryRepository.findById(req.getParentId())
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "ไม่พบหมวดหมู่แม่ ID: " + req.getParentId()));
            category.setParent(parent);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(CategoryDto.from(categoryRepository.save(category)));
    }

    /** PUT /api/admin/categories/{id} — แก้ไขหมวดหมู่ */
    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable Long id, @Valid @RequestBody CategoryRequest req) {

        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "ไม่พบหมวดหมู่ ID: " + id));

        category.setName(req.getName());
        category.setDescription(req.getDescription());
        category.setImageUrl(req.getImageUrl());
        if (req.getActive() != null) category.setActive(req.getActive());

        return ResponseEntity.ok(CategoryDto.from(categoryRepository.save(category)));
    }

    /** DELETE /api/admin/categories/{id} — ลบหมวดหมู่ */
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ไม่พบหมวดหมู่ ID: " + id);
        }
        categoryRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "ลบหมวดหมู่ ID " + id + " สำเร็จ"));
    }

    /** GET /api/admin/stats — สถิติระบบ */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(Map.of(
            "totalProducts", productRepository.count(),
            "totalCategories", categoryRepository.count(),
            "activeProducts", productRepository.findByActiveTrue(Pageable.unpaged()).getTotalElements(),
            "outOfStock", productRepository.findByStockLessThanEqual(0).size()
        ));
    }
}
