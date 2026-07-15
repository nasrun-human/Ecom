package com.example.app.controller;

import com.example.app.dto.RegisterRequest;
import com.example.app.entity.User;
import com.example.app.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Auth API — สมัครสมาชิก, ตรวจสอบ username/email
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** POST /api/auth/register — สมัครสมาชิก */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest req) {

        // ตรวจสอบ username ซ้ำ
        if (userRepository.existsByUsername(req.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "field", "username",
                "message", "ชื่อผู้ใช้ \"" + req.getUsername() + "\" ถูกใช้งานแล้ว"
            ));
        }

        // ตรวจสอบ email ซ้ำ
        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "field", "email",
                "message", "อีเมล \"" + req.getEmail() + "\" ถูกใช้งานแล้ว"
            ));
        }

        // สร้าง User ใหม่
        User user = User.builder()
            .username(req.getUsername())
            .email(req.getEmail())
            .password(passwordEncoder.encode(req.getPassword()))
            .fullName(req.getFullName())
            .phone(req.getPhone())
            .role(User.Role.CUSTOMER)
            .active(true)
            .build();

        User saved = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "success", true,
            "message", "สมัครสมาชิกสำเร็จ! ยินดีต้อนรับ " + saved.getUsername(),
            "userId", saved.getId(),
            "username", saved.getUsername(),
            "email", saved.getEmail(),
            "role", saved.getRole().name()
        ));
    }

    /** GET /api/auth/check-username?username=xxx — ตรวจสอบ username ซ้ำ (real-time) */
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Object>> checkUsername(@RequestParam String username) {
        boolean taken = userRepository.existsByUsername(username);
        return ResponseEntity.ok(Map.of(
            "username", username,
            "available", !taken,
            "message", taken ? "ชื่อผู้ใช้นี้ถูกใช้งานแล้ว" : "ชื่อผู้ใช้นี้ใช้ได้"
        ));
    }

    /** GET /api/auth/check-email?email=xxx — ตรวจสอบ email ซ้ำ (real-time) */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam String email) {
        boolean taken = userRepository.existsByEmail(email);
        return ResponseEntity.ok(Map.of(
            "email", email,
            "available", !taken,
            "message", taken ? "อีเมลนี้ถูกใช้งานแล้ว" : "อีเมลนี้ใช้ได้"
        ));
    }

    /** GET /api/auth/me — ดูข้อมูลตัวเอง (ต้อง login) */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            org.springframework.security.core.Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "กรุณาเข้าสู่ระบบ"));
        }
        return userRepository.findByUsername(auth.getName())
            .map(u -> ResponseEntity.ok((Map<String, Object>) Map.<String, Object>of(
                "id", u.getId(),
                "username", u.getUsername(),
                "email", u.getEmail(),
                "fullName", u.getFullName() != null ? u.getFullName() : "",
                "role", u.getRole().name(),
                "active", u.getActive()
            )))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.<String, Object>of("message", "ไม่พบข้อมูลผู้ใช้")));
    }
}
