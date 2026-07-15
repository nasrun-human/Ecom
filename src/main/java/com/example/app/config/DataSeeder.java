package com.example.app.config;

import com.example.app.entity.Category;
import com.example.app.entity.Product;
import com.example.app.repository.CategoryRepository;
import com.example.app.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

/**
 * DataSeeder — ใส่ข้อมูลตัวอย่างเมื่อตารางยังว่างอยู่
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private final CategoryRepository categoryRepo;
    private final ProductRepository productRepo;
    private final com.example.app.repository.UserRepository userRepo;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedData() {
        return args -> {
            if (userRepo.findByUsername("admin").isEmpty()) {
                log.info("👤 สร้าง Admin User เริ่มต้น...");
                userRepo.save(com.example.app.entity.User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .email("admin@shopverse.com")
                        .fullName("System Administrator")
                        .role(com.example.app.entity.User.Role.ADMIN)
                        .active(true)
                        .build());
            }

            if (categoryRepo.count() > 0) {
                log.info("✅ ข้อมูลสินค้ามีอยู่แล้ว ข้ามการ seed");
                return;
            }

            log.info("🌱 กำลัง seed ข้อมูลตัวอย่าง...");

            // ===== CATEGORIES =====
            Category electronics = categoryRepo.save(Category.builder()
                    .name("อิเล็กทรอนิกส์")
                    .description("สินค้าเทคโนโลยีและอุปกรณ์ไฟฟ้า")
                    .imageUrl("https://images.unsplash.com/photo-1498049794561-7780e7231661?w=400")
                    .active(true).build());

            Category fashion = categoryRepo.save(Category.builder()
                    .name("แฟชั่น")
                    .description("เสื้อผ้า รองเท้า กระเป๋า")
                    .imageUrl("https://images.unsplash.com/photo-1445205170230-053b83016050?w=400")
                    .active(true).build());

            Category food = categoryRepo.save(Category.builder()
                    .name("อาหารและเครื่องดื่ม")
                    .description("อาหาร ขนม เครื่องดื่ม")
                    .imageUrl("https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=400")
                    .active(true).build());

            Category beauty = categoryRepo.save(Category.builder()
                    .name("ความงาม")
                    .description("เครื่องสำอาง ดูแลผิว บำรุงผม")
                    .imageUrl("https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=400")
                    .active(true).build());

            Category sports = categoryRepo.save(Category.builder()
                    .name("กีฬา")
                    .description("อุปกรณ์ออกกำลังกายและกีฬา")
                    .imageUrl("https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=400")
                    .active(true).build());

            Category home = categoryRepo.save(Category.builder()
                    .name("บ้านและสวน")
                    .description("เฟอร์นิเจอร์ ของตกแต่ง เครื่องครัว")
                    .imageUrl("https://images.unsplash.com/photo-1484101403633-562f891dc89a?w=400")
                    .active(true).build());

            // ===== PRODUCTS — Electronics =====
            List<Product> products = List.of(
                Product.builder().name("iPhone 16 Pro Max 256GB")
                    .sku("APPL-IP16PM-256").category(electronics)
                    .price(new BigDecimal("49900")).salePrice(new BigDecimal("44900"))
                    .stock(15).active(true)
                    .description("iPhone รุ่นล่าสุด ชิป A18 Pro กล้อง 48MP")
                    .imageUrl("https://images.unsplash.com/photo-1696446701796-da61225697cc?w=400")
                    .weightKg(new BigDecimal("0.221")).build(),

                Product.builder().name("Samsung Galaxy S25 Ultra")
                    .sku("SAMS-S25U-512").category(electronics)
                    .price(new BigDecimal("44900")).salePrice(null)
                    .stock(8).active(true)
                    .description("Android เรือธง ปากกา S Pen ในตัว RAM 12GB")
                    .imageUrl("https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=400")
                    .weightKg(new BigDecimal("0.218")).build(),

                Product.builder().name("MacBook Air M4 13 นิ้ว")
                    .sku("APPL-MBA-M4-13").category(electronics)
                    .price(new BigDecimal("42900")).salePrice(new BigDecimal("39900"))
                    .stock(5).active(true)
                    .description("โน้ตบุ๊กบางเบา ชิป M4 แบตเตอรี่ 18 ชั่วโมง")
                    .imageUrl("https://images.unsplash.com/photo-1611186871525-d2b48d1a03e4?w=400")
                    .weightKg(new BigDecimal("1.240")).build(),

                Product.builder().name("Sony WH-1000XM6 หูฟัง ANC")
                    .sku("SONY-WH1000XM6").category(electronics)
                    .price(new BigDecimal("12900")).salePrice(new BigDecimal("10900"))
                    .stock(20).active(true)
                    .description("หูฟัง Over-ear ตัดเสียงรบกวนที่ดีที่สุดในตลาด")
                    .imageUrl("https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb?w=400")
                    .weightKg(new BigDecimal("0.254")).build(),

                Product.builder().name("iPad Pro 11\" M4 WiFi 256GB")
                    .sku("APPL-IPADPRO-M4-256").category(electronics)
                    .price(new BigDecimal("32900")).salePrice(null)
                    .stock(12).active(true)
                    .description("แท็บเล็ตพรีเมียม จอ OLED Ultra Retina ชิป M4")
                    .imageUrl("https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400")
                    .weightKg(new BigDecimal("0.444")).build(),

                // ===== PRODUCTS — Fashion =====
                Product.builder().name("Nike Air Max 270 ขาวดำ")
                    .sku("NIKE-AM270-WB-42").category(fashion)
                    .price(new BigDecimal("4500")).salePrice(new BigDecimal("3690"))
                    .stock(30).active(true)
                    .description("รองเท้าผ้าใบ Air Max 270 พื้น Air Unit ใหญ่ที่สุด")
                    .imageUrl("https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400")
                    .weightKg(new BigDecimal("0.380")).build(),

                Product.builder().name("กระเป๋า Canvas Tote Bag")
                    .sku("BAG-CANVAS-TOTE-BK").category(fashion)
                    .price(new BigDecimal("890")).salePrice(null)
                    .stock(50).active(true)
                    .description("กระเป๋าผ้าแคนวาส ทรง Tote สีดำ พิมพ์ลาย")
                    .imageUrl("https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?w=400")
                    .weightKg(new BigDecimal("0.250")).build(),

                Product.builder().name("เสื้อ Oversize Cotton Premium")
                    .sku("SHIRT-OS-CTN-WH-M").category(fashion)
                    .price(new BigDecimal("590")).salePrice(new BigDecimal("490"))
                    .stock(100).active(true)
                    .description("เสื้อ Oversize ผ้า Cotton 100% ใส่สบาย")
                    .imageUrl("https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400")
                    .weightKg(new BigDecimal("0.200")).build(),

                // ===== PRODUCTS — Food =====
                Product.builder().name("กาแฟ Arabica Single Origin 250g")
                    .sku("COFFEE-ARAB-250G").category(food)
                    .price(new BigDecimal("320")).salePrice(null)
                    .stock(200).active(true)
                    .description("เมล็ดกาแฟ Arabica จากดอยช้าง คั่วกลาง บดพร้อมชง")
                    .imageUrl("https://images.unsplash.com/photo-1559056199-641a0ac8b55e?w=400")
                    .weightKg(new BigDecimal("0.250")).build(),

                Product.builder().name("ดาร์กช็อกโกแลต Valrhona 70%")
                    .sku("CHOCO-VALRHONA-70").category(food)
                    .price(new BigDecimal("450")).salePrice(new BigDecimal("380"))
                    .stock(80).active(true)
                    .description("ช็อกโกแลตฝรั่งเศสแท้ โกโก้ 70% ไม่มีสารปรุงแต่ง")
                    .imageUrl("https://images.unsplash.com/photo-1606312619070-d48b4c652a52?w=400")
                    .weightKg(new BigDecimal("0.100")).build(),

                // ===== PRODUCTS — Beauty =====
                Product.builder().name("SK-II Facial Treatment Essence 230ml")
                    .sku("SKII-FTE-230ML").category(beauty)
                    .price(new BigDecimal("5200")).salePrice(new BigDecimal("4590"))
                    .stock(25).active(true)
                    .description("น้ำตบสุดฮิต Pitera™ เข้มข้น ผิวใสกว่าใน 4 สัปดาห์")
                    .imageUrl("https://images.unsplash.com/photo-1571781926291-c477ebfd024b?w=400")
                    .weightKg(new BigDecimal("0.350")).build(),

                Product.builder().name("Innisfree Green Tea Seed Serum 80ml")
                    .sku("INNF-GTS-80ML").category(beauty)
                    .price(new BigDecimal("890")).salePrice(null)
                    .stock(60).active(true)
                    .description("เซรั่มชาเขียว บำรุงผิวชุ่มชื้น ลดริ้วรอยแรกเริ่ม")
                    .imageUrl("https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=400")
                    .weightKg(new BigDecimal("0.150")).build(),

                // ===== PRODUCTS — Sports =====
                Product.builder().name("ดัมเบลปรับน้ำหนักได้ 2-24kg")
                    .sku("SPORT-DUMBELL-24").category(sports)
                    .price(new BigDecimal("3900")).salePrice(new BigDecimal("2990"))
                    .stock(10).active(true)
                    .description("ดัมเบลปรับน้ำหนักได้ 15 ระดับ 2-24kg ประหยัดพื้นที่")
                    .imageUrl("https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=400")
                    .weightKg(new BigDecimal("24.000")).build(),

                Product.builder().name("โยคะแมต TPE 6mm ลายพิมพ์")
                    .sku("SPORT-YOGAMAT-6MM").category(sports)
                    .price(new BigDecimal("690")).salePrice(null)
                    .stock(45).active(true)
                    .description("แผ่นรองโยคะ TPE กันลื่น ดูดซับแรงกระแทก พับเก็บได้")
                    .imageUrl("https://images.unsplash.com/photo-1601925228711-a5e1cf8f1c1d?w=400")
                    .weightKg(new BigDecimal("1.500")).build(),

                // ===== PRODUCTS — Home =====
                Product.builder().name("โคมไฟตั้งโต๊ะ LED ปรับแสงได้")
                    .sku("HOME-LAMP-LED-ADJ").category(home)
                    .price(new BigDecimal("1290")).salePrice(new BigDecimal("990"))
                    .stock(35).active(true)
                    .description("โคมไฟ LED ปรับความสว่างและสีแสงได้ USB-C Charging")
                    .imageUrl("https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=400")
                    .weightKg(new BigDecimal("0.600")).build(),

                Product.builder().name("กระถางดินเผาพร้อมต้นไม้ชุด 3 ใบ")
                    .sku("HOME-POT-TERRA-3PC").category(home)
                    .price(new BigDecimal("590")).salePrice(null)
                    .stock(0).active(true)
                    .description("กระถางดินเผาสไตล์วินเทจ พร้อมดินและต้นไม้ฉ่ำน้ำ")
                    .imageUrl("https://images.unsplash.com/photo-1485955900006-10f4d324d411?w=400")
                    .weightKg(new BigDecimal("1.200")).build()
            );

            productRepo.saveAll(products);
            log.info("✅ Seed สำเร็จ! {} หมวดหมู่, {} สินค้า",
                    categoryRepo.count(), productRepo.count());
        };
    }
}
