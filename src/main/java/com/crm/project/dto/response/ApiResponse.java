package com.crm.project.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
    @Builder.Default
    private int code = 200;

    @Builder.Default
    private String message = "Process succeed";

    private Object data;

    private Object error;

    private Object pagination;
}
