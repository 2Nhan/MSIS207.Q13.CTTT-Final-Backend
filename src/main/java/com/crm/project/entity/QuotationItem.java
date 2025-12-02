package com.crm.project.entity;

import jakarta.persistence.*;
import lombok.*;


import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "quotation_items")
public class QuotationItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private BigDecimal discount;

    @Column(name = "discount_type")
    private String discountType;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    private Integer quantity;

    private BigDecimal subtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_id", nullable = false)
    private Quotation quotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
