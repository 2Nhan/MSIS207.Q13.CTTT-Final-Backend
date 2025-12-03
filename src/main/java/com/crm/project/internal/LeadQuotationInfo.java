package com.crm.project.internal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadQuotationInfo {
    private String id;

    private String fullName;

    private String email;
}
