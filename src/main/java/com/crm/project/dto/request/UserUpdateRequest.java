package com.crm.project.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequest {

    @Size(min = 8, message= "INVALID_PASSWORD")
    private String password;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @Email(message = "INVALID_EMAIL")
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "INVALID_PHONE_NUMBER")
    @JsonProperty("phone_number")
    private String phoneNumber;

    private String address;
}
