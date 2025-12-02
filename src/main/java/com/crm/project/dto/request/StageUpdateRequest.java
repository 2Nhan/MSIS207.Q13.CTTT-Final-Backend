package com.crm.project.dto.request;

import com.crm.project.validator.custom_validator.BlankUpdateConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StageUpdateRequest {
    @BlankUpdateConstraint(message = "BLANK_NAME")
    private String name;

    private String color;
}
