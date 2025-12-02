package com.crm.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuotationCreationRequest {
    @NotBlank
    private String leadId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @Builder.Default
    private LocalDate validUntil = LocalDate.now().plusDays(7);

    @NotNull
    private List<QuotationItemRequest> items;
}
