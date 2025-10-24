package com.crm.project.dto.request;

import com.crm.project.validator.custom_validator.BlankUpdateConstraint;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequest {

    @Size(min = 8, message = "INVALID_PASSWORD")
    private String password;

    @BlankUpdateConstraint(message = "BLANK_FIRSTNAME")
    @JsonProperty("first_name")
    private String firstName;

    @BlankUpdateConstraint(message = "BLANK_LASTNAME")
    @JsonProperty("last_name")
    private String lastName;

    @BlankUpdateConstraint(message = "BLANK_EMAIL")
    @Email(message = "INVALID_EMAIL")
    private String email;

    @BlankUpdateConstraint(message = "BLANK_PHONE_NUMBER")
    @Pattern(regexp = "^[0-9]{10}$", message = "INVALID_PHONE_NUMBER")
    @JsonProperty("phone_number")
    private String phoneNumber;

    @Email(message = "INVALID_EMAIL")
    private String address;
}


