package com.example.app.repository;

import com.example.app.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ใช้ @EntityGraph เพื่อ eager load category ป้องกัน LazyInitializationException
    @EntityGraph(attributePaths = {"category"})
    Page<Product> findByActiveTrue(Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<Product> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Optional<Product> findById(Long id);

    Optional<Product> findBySku(String sku);

    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max AND p.active = true")
    List<Product> findByPriceRange(@Param("min") BigDecimal min, @Param("max") BigDecimal max);

    List<Product> findByStockLessThanEqual(Integer threshold);
}
