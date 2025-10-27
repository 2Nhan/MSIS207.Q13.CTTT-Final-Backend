package com.crm.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "order_code", nullable = false, unique = true)
    private String orderCode;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "billing_address", nullable = false)
    private String billingAddress;

    @Column(name = "shipping_address", nullable = false)
    private String shippingAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "sales_channel", nullable = false)
    private SalesChannel salesChannel;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "total_amount", precision = 12, scale = 4, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "order_at")
    private LocalDateTime orderAt;
    
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public enum SalesChannel {
        E_COMMERCE,
        IN_STORE,
        AGENT,
        PROJECT,
        SERVICE
    }

    public enum PaymentStatus {
        UNPAID,
        PARTIALLY_PAID,
        PAID,
        REFUNDED
    }
}
