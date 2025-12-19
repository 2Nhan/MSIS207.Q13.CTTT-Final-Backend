package com.crm.project.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivitySummaryResponse {
    private String id;
    private String type;
    private String content;
    private LocalDate validUntil;
    private String status;
    private boolean completed;
    private LocalDateTime createdAt;

    // Thông tin lead context
    private String leadId;
    private String leadName;
    private String leadCompany;
}
