package com.crm.project.dto.response;

import lombok.*;

import java.util.List;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChartResponse {
    private String name;
    private BigDecimal currentAmount;
    private BigDecimal pastAmount;
    private Double percentage;
    private String status;
    private List<ChartDataPoint> chartData;
}
