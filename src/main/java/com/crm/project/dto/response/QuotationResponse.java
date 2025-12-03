package com.crm.project.dto.response;

import com.crm.project.internal.LeadQuotationInfo;
import com.crm.project.internal.QuotationItemInfo;
import com.crm.project.internal.UserNormalInfo;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotationResponse {
    private String id;

    private LeadQuotationInfo lead;

    private String title;

    private String content;

    private LocalDate validUntil;

    private String status;

    private BigDecimal total;

    private List<QuotationItemInfo> items;

    private UserNormalInfo createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
