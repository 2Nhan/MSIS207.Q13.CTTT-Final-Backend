package com.crm.project.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppErrorResponse {
    private int code;

    @JsonProperty("error_field")
    private String errorField;

    @JsonProperty("error_param")
    private String errorParam;

    private String message;
}
