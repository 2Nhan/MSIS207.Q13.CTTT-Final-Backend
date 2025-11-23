package com.crm.project.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeadCreationRequest {

    @NotBlank(message = "BLANK_FULLNAME")
    private String fullName;

    private LocalDate dateOfBirth;

    private String gender;

    @NotBlank(message = "BLANK_EMAIL")
    @Email(message = "INVALID_EMAIL")
    private String email;

    @NotBlank(message = "BLANK_PHONE_NUMBER")
    @Pattern(regexp = "^[0-9]{10}$", message = "INVALID_PHONE_NUMBER")
    private String phoneNumber;

    @Max(value = 3, message = "INVALID_RATING")
    private Integer rating;

    private BigDecimal expectedRevenue;
}
