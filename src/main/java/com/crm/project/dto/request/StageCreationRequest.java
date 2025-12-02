package com.crm.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StageCreationRequest {
    @NotBlank(message = "BLANK_NAME")
    private String name;

    private String color;
}
