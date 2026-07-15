package com.example.app.service;

import com.example.app.entity.*;
import com.example.app.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final StockService stockService;

    private final AtomicInteger orderSeq = new AtomicInteger(1);

    /**
     * สร้าง Order จากตะกร้าสินค้า (Guest checkout ได้)
     * items = [{productId, quantity}]
     */
    @Transactional
    public Order createOrder(List<Map<String, Object>> items, String shippingAddress, String note) {
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (Map<String, Object> item : items) {
            Long productId = Long.valueOf(item.get("productId").toString());
            int qty = Integer.parseInt(item.get("quantity").toString());

            Product p = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "ไม่พบสินค้า ID: " + productId));

            if (!p.getActive())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "สินค้า " + p.getName() + " ไม่พร้อมขาย");
            if (p.getStock() < qty)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "สต๊อก " + p.getName() + " ไม่พอ (มี " + p.getStock() + ")");

            BigDecimal unitPrice = p.getSalePrice() != null ? p.getSalePrice() : p.getPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(qty));
            subtotal = subtotal.add(lineTotal);

            orderItems.add(OrderItem.builder()
                .product(p)
                .productName(p.getName())
                .productSku(p.getSku())
                .quantity(qty)
                .unitPrice(unitPrice)
                .totalPrice(lineTotal)
                .build());
        }

        BigDecimal shipping = subtotal.compareTo(new BigDecimal("500")) >= 0
            ? BigDecimal.ZERO : new BigDecimal("50");
        BigDecimal total = subtotal.add(shipping);

        Order order = Order.builder()
            .orderNumber(generateOrderNumber())
            .status(Order.OrderStatus.PENDING)
            .paymentStatus(Order.PaymentStatus.UNPAID)
            .paymentMethod(Order.PaymentMethod.PROMPTPAY)
            .subtotal(subtotal)
            .shippingFee(shipping)
            .discountAmount(BigDecimal.ZERO)
            .totalAmount(total)
            .shippingAddress(shippingAddress)
            .note(note)
            .build();

        Order saved = orderRepository.save(order);
        orderItems.forEach(i -> { i.setOrder(saved); });
        saved.setItems(orderItems);
        Order finalOrder = orderRepository.save(saved);
        log.info("🛒 สร้าง Order {} ยอด ฿{}", finalOrder.getOrderNumber(), total);
        return finalOrder;
    }

    /** Admin ยืนยันชำระเงิน → ตัดสต๊อกอัตโนมัติ */
    @Transactional
    public Order confirmPayment(Long orderId, String confirmedBy) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ไม่พบ Order"));

        if (order.getPaymentStatus() == Order.PaymentStatus.PAID)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ยืนยันชำระเงินแล้ว");

        order.setPaymentStatus(Order.PaymentStatus.PAID);
        order.setStatus(Order.OrderStatus.CONFIRMED);

        // ตัดสต๊อกทุกรายการ
        for (OrderItem item : order.getItems()) {
            stockService.deductStock(
                item.getProduct().getId(),
                item.getQuantity(),
                "ขาย Order: " + order.getOrderNumber(),
                orderId,
                confirmedBy
            );
        }

        Order saved = orderRepository.save(order);
        log.info("✅ ยืนยันชำระเงิน Order {} โดย {}", order.getOrderNumber(), confirmedBy);
        return saved;
    }

    /** อัปเดตสถานะ Order */
    @Transactional
    public Order updateStatus(Long orderId, Order.OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ไม่พบ Order"));
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    private String generateOrderNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("ORD-%s-%04d", date, orderSeq.getAndIncrement());
    }
}
