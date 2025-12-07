package com.crm.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityCreationRequest {

    @NotBlank(message = "BLANK_ACTIVITY_TYPE")
    private String type;

    private String content;

    @Builder.Default
    private LocalDate validUntil = LocalDate.now().plusDays(7);
}
