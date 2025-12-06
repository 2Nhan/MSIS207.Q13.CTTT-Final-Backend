package com.crm.project.dto.request;

import com.crm.project.validator.custom_validator.BlankUpdateConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadUpdateRequest {

    @BlankUpdateConstraint(message = "BLANK_FULLNAME")
    private String fullName;

    private LocalDate dateOfBirth;

    @BlankUpdateConstraint(message = "BLANK_EMAIL")
    @Email(message = "INVALID_EMAIL")
    private String email;

    @BlankUpdateConstraint(message = "BLANK_PHONE_NUMBER")
    @Pattern(regexp = "^[0-9]{10}$", message = "INVALID_PHONE_NUMBER")
    private String phoneNumber;

    private String company;

    private LocalDate closingDate;

    @Max(value = 5, message = "INVALID_RATING")
    private Integer rating;

    private BigDecimal expectedRevenue;

    private String note;

    private MultipartFile image;
}
