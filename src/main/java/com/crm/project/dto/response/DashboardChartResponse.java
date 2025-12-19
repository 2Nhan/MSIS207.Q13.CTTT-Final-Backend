package com.crm.project.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardChartResponse {
    private ChartResponse orderRevenue;
    private ChartResponse leadConversion;
    private ChartResponse quotationsSent;
    private ChartResponse tasksCompleted;
}
