package com.crm.project.dto.response;

import com.crm.project.internal.QuotationItemInfo;
import com.crm.project.internal.UserNormalInfo;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotationResponse {
    private String leadId;

    private String leadName;

    private String title;

    private String content;

    private LocalDate validUntil;

    private String status;

    private BigDecimal total;

    private QuotationItemInfo items;

    private UserNormalInfo createdBy;

    private LocalDate createdDate;

    private LocalDate updatedDate;
}
