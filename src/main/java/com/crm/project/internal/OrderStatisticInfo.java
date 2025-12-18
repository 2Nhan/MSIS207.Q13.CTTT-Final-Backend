package com.crm.project.internal;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderStatisticInfo {
    private Long totalOrders;
    private BigDecimal totalSpent;
    private Double averageOrder;
    private LocalDateTime lastOrderAt;
}
