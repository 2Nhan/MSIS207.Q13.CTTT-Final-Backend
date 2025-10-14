package com.crm.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {
    @NotBlank(message = "BLANK_USERNAME")
    private String username;

    @NotBlank(message = "BLANK_PASSWORD")
    private String password;
}
