package com.crm.project.dto.response;

import com.crm.project.internal.OrderStatisticInfo;
import com.crm.project.internal.UserNormalInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerResponse {

    private String id;

    private String fullName;

    private LocalDate dateOfBirth;

    private String email;

    private String phoneNumber;

    private String company;

    private Integer rating;

    private String avatarUrl;

    @JsonProperty("assignTo")
    private UserNormalInfo user;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private OrderStatisticInfo orderStatisticInfo;
}
