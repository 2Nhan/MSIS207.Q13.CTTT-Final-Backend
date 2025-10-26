package com.crm.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "order_items")
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(precision = 12, scale = 4, nullable = false)
    private BigDecimal price;

    @Column(name = "discount_percent", precision = 12, scale = 4)
    private BigDecimal discountPercent;

    @Column(name = "discount_amount", precision = 12, scale = 4)
    private BigDecimal discountAmount;

    @Column(name = "tax_percent", precision = 12, scale = 4)
    private BigDecimal taxPercent;

    @Column(name = "tax_amount", precision = 12, scale = 4)
    private BigDecimal taxAmount;

    @Column(precision = 12, scale = 4, nullable = false)
    private BigDecimal total;
}
