package com.crm.project.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardSummaryResponse {
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long totalTasks;
    private Long totalLeads;
}
