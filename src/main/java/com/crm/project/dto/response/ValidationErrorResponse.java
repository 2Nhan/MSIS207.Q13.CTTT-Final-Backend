package com.crm.project.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidationErrorResponse {
    private int code;

    @JsonProperty("error_field")
    private String errorField;

    private String message;
}
