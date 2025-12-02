package com.crm.project.internal;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotationItemInfo {
    private String id;

    private String name;

    private BigDecimal discount;

    private String discountType;

    private BigDecimal unitPrice;

    private Integer quantity;

    private BigDecimal subtotal;
}
