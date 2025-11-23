package com.crm.project.dto.response;

import com.crm.project.internal.LeadNormalInfo;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StagesWithLeadsResponse {
    private String id;

    private String name;

    private List<LeadNormalInfo> leads;
}
