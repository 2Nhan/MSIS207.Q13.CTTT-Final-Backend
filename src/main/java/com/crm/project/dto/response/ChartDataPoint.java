package com.crm.project.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChartDataPoint {
    private String name;
    private BigDecimal current;
    private BigDecimal past;
}
