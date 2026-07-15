package com.example.app.controller;

import com.example.app.entity.Order;
import com.example.app.entity.OrderItem;
import com.example.app.repository.OrderRepository;
import com.example.app.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    /** POST /api/orders — สร้าง Order */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
        String address = (String) body.getOrDefault("shippingAddress", "");
        String note = (String) body.getOrDefault("note", "");

        if (items == null || items.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ไม่มีสินค้าในตะกร้า");

        Order order = orderService.createOrder(items, address, note);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "success", true,
            "orderId", order.getId(),
            "orderNumber", order.getOrderNumber(),
            "totalAmount", order.getTotalAmount(),
            "shippingFee", order.getShippingFee(),
            "subtotal", order.getSubtotal(),
            "status", order.getStatus().name(),
            "message", "สร้างคำสั่งซื้อสำเร็จ! กรุณาชำระเงินผ่าน PromptPay"
        ));
    }

    /** GET /api/orders/{id} — ดูรายละเอียด Order */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrder(@PathVariable Long id) {
        Order o = orderRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ไม่พบ Order"));
        return ResponseEntity.ok(toDetail(o));
    }

    /** GET /api/orders/number/{orderNumber} */
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<Map<String, Object>> getByNumber(@PathVariable String orderNumber) {
        Order o = orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ไม่พบ Order " + orderNumber));
        return ResponseEntity.ok(toDetail(o));
    }

    private Map<String, Object> toDetail(Order o) {
        List<Map<String, Object>> items = o.getItems().stream().map(i -> Map.<String,Object>of(
            "productId", i.getProduct() != null ? i.getProduct().getId() : 0,
            "productName", i.getProductName(),
            "productSku", i.getProductSku(),
            "quantity", i.getQuantity(),
            "unitPrice", i.getUnitPrice(),
            "totalPrice", i.getTotalPrice()
        )).toList();

        Map<String, Object> map = new java.util.LinkedHashMap<>();
        map.put("id", o.getId());
        map.put("orderNumber", o.getOrderNumber());
        map.put("status", o.getStatus().name());
        map.put("paymentStatus", o.getPaymentStatus().name());
        map.put("paymentMethod", o.getPaymentMethod() != null ? o.getPaymentMethod().name() : "");
        map.put("subtotal", o.getSubtotal());
        map.put("shippingFee", o.getShippingFee());
        map.put("totalAmount", o.getTotalAmount());
        map.put("shippingAddress", o.getShippingAddress() != null ? o.getShippingAddress() : "");
        map.put("items", items);
        map.put("createdAt", o.getCreatedAt() != null ? o.getCreatedAt().toString() : "");
        return map;
    }
}
