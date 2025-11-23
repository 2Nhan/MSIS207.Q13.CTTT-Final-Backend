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
@JsonPropertyOrder({
        "id",
        "fullName",
        "dateOfBirth",
        "gender",
        "email",
        "phoneNumber",
        "rating",
        "expectedRevenue",
        "avatarUrl",
        "stage",
        "responsibleBy",
        "createdAt",
        "updatedAt"
})
public class LeadResponse {

    private String id;

    private String fullName;

    private LocalDate dateOfBirth;

    private String gender;

    private String email;

    private String phoneNumber;

    private Integer rating;

    private BigDecimal expectedRevenue;

    private String avatarUrl;

    private StageResponse stage;

    @JsonProperty("responsibleBy")
    private UserNormalInfo user;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
