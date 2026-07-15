package com.example.app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SchemaUpdater implements CommandLineRunner {
    private final JdbcTemplate jdbcTemplate;

    public SchemaUpdater(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            jdbcTemplate.execute("ALTER TABLE orders ALTER COLUMN user_id DROP NOT NULL;");
            System.out.println("✅ แก้ไขให้ตาราง orders สามารถมี user_id เป็น null ได้สำเร็จ");
        } catch (Exception e) {
            System.out.println("⚠️ ไม่สามารถแก้ constraint ของ orders ได้ (อาจจะถูกแก้ไปแล้ว): " + e.getMessage());
        }
    }
}
