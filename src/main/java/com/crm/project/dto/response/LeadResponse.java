package com.crm.project.dto.response;

import com.crm.project.internal.UserNormalInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeadResponse {

    private String id;

    private String fullName;

    private LocalDate dateOfBirth;

    private String email;

    private String phoneNumber;

    private String company;

    private LocalDate closingDate;

    private Integer rating;

    private BigDecimal expectedRevenue;

    private String note;

    private String avatarUrl;

    private StageResponse stage;

    @JsonProperty("assignTo")
    private UserNormalInfo user;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
