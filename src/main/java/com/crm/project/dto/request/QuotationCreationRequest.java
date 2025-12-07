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
    @NotBlank(message = "BLANK_LEAD_ID")
    private String leadId;

    @NotBlank(message = "BLANK_TITLE")
    private String title;

    private String content;

    @Builder.Default
    private LocalDate validUntil = LocalDate.now().plusDays(7);

    @NotNull(message = "BLANK_ITEMS")
    private List<QuotationItemRequest> items;
}
