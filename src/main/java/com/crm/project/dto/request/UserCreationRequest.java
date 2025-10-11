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
public class UserCreationRequest {
    @NotBlank(message = "BLANK_USERNAME")
    @Size(min = 8, max = 100, message = "INVALID_USERNAME")
    private String username;

    @NotBlank(message = "BLANK_PASSWORD")
    @Size(min = 8, message= "INVALID_PASSWORD")
    private String password;

    @NotBlank(message = "BLANK_FIRSTNAME")
    @JsonProperty("first_name")
    private String firstName;

    @NotBlank(message = "BLANK_LASTNAME")
    @JsonProperty("last_name")
    private String lastName;

    @NotBlank(message = "BLANK_EMAIL")
    @Email(message = "INVALID_EMAIL")
    private String email;

    @NotBlank(message = "BLANK_PHONE_NUMBER")
    @Pattern(regexp = "^[0-9]{10}$", message = "INVALID_PHONE_NUMBER")
    @JsonProperty("phone_number")
    private String phoneNumber;

    private String address;
}
