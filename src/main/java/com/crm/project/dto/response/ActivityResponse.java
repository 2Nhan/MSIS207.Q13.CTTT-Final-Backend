package com.crm.project.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityResponse {
    private String id;

    private String type;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDate validUntil;

    private String status;

    private boolean completed;
}
