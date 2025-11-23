package com.crm.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StageUpdateRequest {
    @NotBlank(message = "BLANK_NAME")
    private String name;
}
