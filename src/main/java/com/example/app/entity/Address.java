package com.example.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * ตาราง addresses — ที่อยู่จัดส่งของผู้ใช้
 */
@Entity
@Table(name = "addresses")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(name = "recipient_name", nullable = false, length = 200)
    private String recipientName;

    @Column(length = 20)
    private String phone;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;  // บ้านเลขที่ ถนน

    @NotBlank
    @Column(nullable = false, length = 100)
    private String district;   // อำเภอ/เขต

    @NotBlank
    @Column(nullable = false, length = 100)
    private String province;   // จังหวัด

    @NotBlank
    @Column(name = "postal_code", nullable = false, length = 10)
    private String postalCode;

    @Column(nullable = false)
    @Builder.Default
    private String country = "Thailand";

    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
