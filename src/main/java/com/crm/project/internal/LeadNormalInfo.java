package com.crm.project.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadNormalInfo {
    private String id;
    private String fullName;
    private BigDecimal expectedRevenue;
    private Integer rating;
    @JsonProperty("responsibleBy")
    private UserNormalInfo user;
}
