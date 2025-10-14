package com.crm.project.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationResponse {
    private boolean authenticated;

    @JsonProperty("access_token")
    private String accessToken;
}
