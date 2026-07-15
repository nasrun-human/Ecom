package com.example.app.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/** Request สำหรับสมัครสมาชิก */
@Data
public class RegisterRequest {

    @NotBlank(message = "ชื่อผู้ใช้ห้ามว่าง")
    @Size(min = 3, max = 100, message = "ชื่อผู้ใช้ต้อง 3-100 ตัวอักษร")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "ชื่อผู้ใช้ใช้ได้เฉพาะ a-z, 0-9, _")
    private String username;

    @NotBlank(message = "อีเมลห้ามว่าง")
    @Email(message = "รูปแบบอีเมลไม่ถูกต้อง")
    private String email;

    @NotBlank(message = "รหัสผ่านห้ามว่าง")
    @Size(min = 6, max = 100, message = "รหัสผ่านต้องอย่างน้อย 6 ตัวอักษร")
    private String password;

    @Size(max = 200)
    private String fullName;

    @Pattern(regexp = "^[0-9]{9,10}$", message = "เบอร์โทรต้องเป็นตัวเลข 9-10 หลัก")
    private String phone;
}
