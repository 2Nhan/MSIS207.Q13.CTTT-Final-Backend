package com.crm.project.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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

    @NotBlank(message = "BLANK_EMAIL")
    @Email(message = "INVALID_EMAIL")
    private String email;

    @NotBlank(message = "BLANK_PHONE_NUMBER")
    @Pattern(regexp = "^[0-9]{10}$", message = "INVALID_PHONE_NUMBER")
    private String phoneNumber;

    private String company;

    private LocalDate closingDate;

    @Max(value = 5, message = "INVALID_RATING")
    private Integer rating;

    private BigDecimal expectedRevenue;

    private String note;

    private String source;

    private MultipartFile image;
}
