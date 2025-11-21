package com.crm.project.dto.response;

import com.crm.project.entity.Stage;
import com.crm.project.entity.User;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeadResponse {

    private String id;

    private String fullName;

    private LocalDate dateOfBirth;

    private String gender;

    private String email;

    private String phoneNumber;

    private String avatarUrl;

    private Integer rating;

    private BigDecimal expectedRevenue;

    private StageResponse stage;

    private UserResponse user;
}
