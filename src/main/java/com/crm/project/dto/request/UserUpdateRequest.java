package com.crm.project.dto.request;

import com.crm.project.validator.custom_validator.BlankUpdateConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequest {

    @Size(min = 8, message = "INVALID_PASSWORD")
    private String password;

    @BlankUpdateConstraint(message = "BLANK_FIRSTNAME")
    private String firstName;

    @BlankUpdateConstraint(message = "BLANK_LASTNAME")
    private String lastName;

    @BlankUpdateConstraint(message = "BLANK_EMAIL")
    @Email(message = "INVALID_EMAIL")
    private String email;

    @BlankUpdateConstraint(message = "BLANK_PHONE_NUMBER")
    @Pattern(regexp = "^[0-9]{10}$", message = "INVALID_PHONE_NUMBER")
    private String phoneNumber;

    private String address;

    private LocalDate dateOfBirth;
}


