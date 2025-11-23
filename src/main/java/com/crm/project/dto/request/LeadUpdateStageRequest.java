package com.crm.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadUpdateStageRequest {
    @NotBlank(message = "BLANK_STAGE_ID")
    private String stageId;
}
