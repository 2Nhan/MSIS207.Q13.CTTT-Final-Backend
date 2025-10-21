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

    @JsonProperty("field_error")
    private String fieldError;

    private String message;
}
